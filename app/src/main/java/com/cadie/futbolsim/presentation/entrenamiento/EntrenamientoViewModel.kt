package com.cadie.futbolsim.presentation.entrenamiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class ResumenJornada(
    val jornada: Int,
    val precision: Double,
    val errorTotal: Double
)

sealed class EntrenamientoUiState {
    object Ejecutando : EntrenamientoUiState()
    data class Completado(val resumen: List<ResumenJornada>) : EntrenamientoUiState()
    object Error : EntrenamientoUiState()
}

@HiltViewModel
class EntrenamientoViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<EntrenamientoUiState>(EntrenamientoUiState.Ejecutando)
    val uiState: StateFlow<EntrenamientoUiState> = _uiState.asStateFlow()

    init {
        observarProgreso()
    }

    fun observarProgreso() {
        _uiState.value = EntrenamientoUiState.Ejecutando
        
        viewModelScope.launch {
            // Simulación del progreso (en producción consulta a Supabase cada 10s)
            var attempts = 0
            while (isActive && attempts < 5) {
                delay(2000) // 2 segundos para demo, deberian ser 10s en prod
                attempts++
            }

            // Generar datos simulados del entrenamiento para la gráfica
            val resumen = (1..38).map { j ->
                ResumenJornada(
                    jornada = j,
                    precision = 40.0 + (j * 1.2) + Random.nextDouble(-2.0, 2.0),
                    errorTotal = 10.0 - (j * 0.2)
                )
            }
            
            _uiState.value = EntrenamientoUiState.Completado(resumen)
        }
    }
}
