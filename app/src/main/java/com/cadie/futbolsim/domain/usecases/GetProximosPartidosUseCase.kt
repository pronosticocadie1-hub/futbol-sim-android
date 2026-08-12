package com.cadie.futbolsim.domain.usecases

import com.cadie.futbolsim.data.repository.IPartidoRepository
import com.cadie.futbolsim.domain.models.Partido
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProximosPartidosUseCase @Inject constructor(
    private val repository: IPartidoRepository
) {
    operator fun invoke(liga: String?): Flow<Result<List<Partido>>> {
        return repository.getProximosPartidos(liga).map { partidos ->
            if (partidos.isEmpty()) {
                // Podría intentar forzar actualización aquí, o depender del ViewModel para manejarlo
                Result.success(emptyList()) // Simplificado para que el VM gestione si está vacío
            } else {
                Result.success(partidos)
            }
        }
    }
}
