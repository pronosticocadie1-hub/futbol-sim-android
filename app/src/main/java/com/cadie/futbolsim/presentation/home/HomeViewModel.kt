package com.cadie.futbolsim.presentation.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cadie.futbolsim.domain.usecase.GetProximosPartidosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val partidos: List<Any>, // Reemplazar con el modelo real de Partido
        val ligaSeleccionada: String?,
        val isOffline: Boolean
    ) : HomeUiState()
    data class Error(val msg: String) : HomeUiState()
    object Refreshing : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProximosPartidosUseCase: GetProximosPartidosUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentLiga: String? = null

    init {
        checkInitialTraining()
        cargarPartidos()
    }

    private fun checkInitialTraining() {
        // En una app real, revisarías prefs para ver si entrenamiento_inicial_completado es true
        // Si es false, se deberia emitir un evento para navegar.
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    fun cargarPartidos(liga: String? = null) {
        _uiState.value = HomeUiState.Loading
        currentLiga = liga

        viewModelScope.launch {
            try {
                // val partidos = getProximosPartidosUseCase.execute(liga)
                val partidos = emptyList<Any>() // mock
                val isOffline = !isNetworkAvailable()
                
                _uiState.value = HomeUiState.Success(partidos, currentLiga, isOffline)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Error al cargar partidos: ${e.message}")
            }
        }
    }

    fun seleccionarLiga(liga: String?) {
        cargarPartidos(liga)
    }

    fun actualizar() {
        if (!isNetworkAvailable()) {
            val current = _uiState.value
            if (current is HomeUiState.Success) {
                _uiState.value = current.copy(isOffline = true)
            }
            return
        }
        
        _uiState.value = HomeUiState.Refreshing
        viewModelScope.launch {
            try {
                // repository.forzarActualizacion()
                cargarPartidos(currentLiga)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Error actualizando: ${e.message}")
            }
        }
    }
}
