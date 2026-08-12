package com.cadie.futbolsim.presentation.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cadie.futbolsim.utils.EncryptedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SetupUiState {
    object Idle : SetupUiState()
    object Verifying : SetupUiState()
    object AllValid : SetupUiState()
    data class PartialValid(val failing: List<String>) : SetupUiState()
    object Saved : SetupUiState()
    data class Error(val msg: String) : SetupUiState()
}

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val prefs: EncryptedPrefs
) : ViewModel() {

    private val _uiState = MutableStateFlow<SetupUiState>(SetupUiState.Idle)
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    fun cargarDesdeSupabase() {
        if (prefs.hasApiKeys()) {
            _uiState.value = SetupUiState.AllValid
        }
    }

    fun verificarYGuardar(keys: Map<String, String>) {
        val supabaseUrl = keys["supabaseUrl"] ?: ""
        val supabaseKey = keys["supabaseKey"] ?: ""
        val backendUrl = keys["backendUrl"] ?: ""

        if (supabaseUrl.isBlank() || supabaseKey.isBlank() || backendUrl.isBlank()) {
            _uiState.value = SetupUiState.Error("Faltan campos obligatorios (Supabase o Backend URL).")
            return
        }

        _uiState.value = SetupUiState.Verifying
        
        viewModelScope.launch {
            delay(1500) // Simulating verification

            try {
                // Guarda claves
                prefs.saveSupabaseUrl(supabaseUrl)
                prefs.saveSupabaseKey(supabaseKey)
                prefs.saveBackendUrl(backendUrl)
                
                keys.forEach { (k, v) ->
                    if (k !in listOf("supabaseUrl", "supabaseKey", "backendUrl") && v.isNotBlank()) {
                        prefs.saveApiKey(k, v)
                    }
                }

                prefs.setFirstRun(false)

                val emptyOptionals = keys.filter { 
                    it.key !in listOf("supabaseUrl", "supabaseKey", "backendUrl") && it.value.isBlank() 
                }.keys.toList()

                if (emptyOptionals.isNotEmpty()) {
                    _uiState.value = SetupUiState.PartialValid(emptyOptionals)
                    delay(2000)
                }
                
                _uiState.value = SetupUiState.Saved
            } catch (e: Exception) {
                _uiState.value = SetupUiState.Error("Error al guardar: ${e.message}")
            }
        }
    }
}
