package com.cadie.futbolsim.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SettingsUiState {
    object Idle : SettingsUiState()
    object Saving : SettingsUiState()
    object Saved : SettingsUiState()
    object Resetting : SettingsUiState()
    object ResetDone : SettingsUiState()
    data class Error(val msg: String) : SettingsUiState()
}

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _claves = MutableStateFlow<Map<String, String>>(emptyMap())
    val claves: StateFlow<Map<String, String>> = _claves.asStateFlow()

    init {
        cargarClaves()
    }

    fun cargarClaves() {
        viewModelScope.launch {
            // Mock EncryptedPrefs logic
            val mockKeys = mapOf(
                "API_KEY" to maskKey("1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
                "SUPABASE_URL" to "https://abc.supabase.co"
            )
            _claves.value = mockKeys
        }
    }

    private fun maskKey(key: String): String {
        if (key.length <= 8) return "********"
        val start = key.take(4)
        val end = key.takeLast(4)
        return "$start${"*".repeat(key.length - 8)}$end"
    }

    fun guardarClaves(keys: Map<String, String>) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Saving
            try {
                // Mock save logic
                delay(1000)
                _claves.value = keys.mapValues { maskKey(it.value) }
                _uiState.value = SettingsUiState.Saved
                delay(2000)
                _uiState.value = SettingsUiState.Idle
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "Error al guardar")
            }
        }
    }

    fun resetApp() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Resetting
            try {
                // Mock reset logic (Room, Prefs)
                delay(1500)
                _uiState.value = SettingsUiState.ResetDone
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "Error al resetear")
            }
        }
    }
}
