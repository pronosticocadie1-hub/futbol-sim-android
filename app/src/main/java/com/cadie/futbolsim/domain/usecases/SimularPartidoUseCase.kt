package com.cadie.futbolsim.domain.usecases

import com.cadie.futbolsim.data.repository.IPartidoRepository
import com.cadie.futbolsim.domain.models.Partido
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SimularPartidoUseCase @Inject constructor(
    private val repository: IPartidoRepository
) {
    suspend operator fun invoke(partidoId: String): Result<Partido> {
        return try {
            val partidoActual = repository.getPartidoById(partidoId).firstOrNull()
            
            if (partidoActual != null && partidoActual.tieneSimulacion && partidoActual.fechaSimulacion != null) {
                val tiempoTranscurrido = System.currentTimeMillis() - partidoActual.fechaSimulacion
                val veinticuatroHoras = 24 * 60 * 60 * 1000L
                
                if (tiempoTranscurrido < veinticuatroHoras) {
                    return Result.success(partidoActual)
                }
            }

            val resultado = repository.simularPartido(partidoId)
            Result.success(resultado)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
