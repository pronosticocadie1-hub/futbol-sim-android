package com.cadie.futbolsim.presentation.login

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

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val msg: String) : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val prefs: EncryptedPrefs
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun checkSavedLogin() {
        if (prefs.isLoggedIn()) {
            _uiState.value = LoginUiState.Success
        }
    }

    fun login(usuario: String, contrasena: String) {
        _uiState.value = LoginUiState.Loading
        
        viewModelScope.launch {
            delay(1000) // Simulate network delay
            
            if (usuario == "CADIE24" && contrasena == "7642Cadie24") {
                prefs.setLoggedIn(true)
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error("Credenciales incorrectas")
            }
        }
    }
}
