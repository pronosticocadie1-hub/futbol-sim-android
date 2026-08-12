package com.cadie.futbolsim.presentation.simulacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cadie.futbolsim.presentation.home.Partido
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SimulacionUiState {
    object Loading : SimulacionUiState()
    data class Success(val partido: Partido) : SimulacionUiState()
    data class NeedConfirmation(val partido: Partido) : SimulacionUiState()
    data class Error(val msg: String) : SimulacionUiState()
}

@HiltViewModel
class SimulacionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<SimulacionUiState>(SimulacionUiState.Loading)
    val uiState: StateFlow<SimulacionUiState> = _uiState.asStateFlow()

    fun cargarSimulacion(partidoId: String) {
        viewModelScope.launch {
            _uiState.value = SimulacionUiState.Loading
            try {
                // Mock behavior for fetching data
                delay(1000)
                val mockPartido = Partido(partidoId, "Local", "Visitante", "", "", "12 Oct 2024", "2-1", 85)
                
                // Emulate logic
                val hasRecentSim = true 
                val hasNewData = false

                if (hasRecentSim && !hasNewData) {
                    _uiState.value = SimulacionUiState.Success(mockPartido)
                } else if (hasRecentSim && hasNewData) {
                    _uiState.value = SimulacionUiState.NeedConfirmation(mockPartido)
                } else {
                    // Call SimularPartidoUseCase
                    _uiState.value = SimulacionUiState.Success(mockPartido)
                }
            } catch (e: Exception) {
                _uiState.value = SimulacionUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun confirmarRecalculo(partidoId: String) {
        viewModelScope.launch {
            _uiState.value = SimulacionUiState.Loading
            delay(1000) // mock recalculo
            val mockPartido = Partido(partidoId, "Local", "Visitante", "", "", "12 Oct 2024", "3-1", 90)
            _uiState.value = SimulacionUiState.Success(mockPartido)
        }
    }

    fun rechazarRecalculo(partido: Partido) {
        _uiState.value = SimulacionUiState.Success(partido)
    }
}
