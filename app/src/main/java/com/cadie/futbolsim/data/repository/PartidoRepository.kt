package com.cadie.futbolsim.data.repository

import android.content.SharedPreferences
import com.cadie.futbolsim.data.local.dao.PartidoDao
import com.cadie.futbolsim.data.remote.BackendApi
import com.cadie.futbolsim.data.remote.EstadoSistemaDto
import com.cadie.futbolsim.data.remote.ISupabaseClient
import com.cadie.futbolsim.data.remote.PartidoDto
import com.cadie.futbolsim.data.remote.SimularRequest
import com.cadie.futbolsim.data.remote.SimulacionDto
import com.cadie.futbolsim.domain.models.Partido
import com.cadie.futbolsim.domain.models.toDomain
import com.cadie.futbolsim.domain.models.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface IPartidoRepository {
    fun getProximosPartidos(liga: String?): Flow<List<Partido>>
    fun getPartidoById(id: String): Flow<Partido?>
    suspend fun simularPartido(id: String): Partido
    suspend fun sincronizarConSupabase(): Result<Unit>
    suspend fun forzarActualizacion(liga: String?): Result<Unit>
    suspend fun getEstadoSistema(): EstadoSistemaDto?
}

class PartidoRepositoryImpl @Inject constructor(
    private val dao: PartidoDao,
    private val supabaseClient: ISupabaseClient,
    private val backendApi: BackendApi,
    private val prefs: SharedPreferences
) : IPartidoRepository {

    override fun getProximosPartidos(liga: String?): Flow<List<Partido>> {
        return if (liga != null) {
            dao.getProximosPartidos(liga, 50).map { entities -> entities.map { it.toDomain() } }
        } else {
            dao.getProximosPartidosTodas(50).map { entities -> entities.map { it.toDomain() } }
        }
    }

    override fun getPartidoById(id: String): Flow<Partido?> {
        return dao.getPartidoById(id).map { it?.toDomain() }
    }

    override suspend fun simularPartido(id: String): Partido = withContext(Dispatchers.IO) {
        val response = backendApi.simularPartido(SimularRequest(partido_id = id))
        if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!
            dao.updateSimulacion(
                id = id,
                goles_local_sim = body.goles_local_sim,
                goles_visitante_sim = body.goles_visitante_sim,
                remates_local_sim = body.remates_local_sim,
                remates_visitante_sim = body.remates_visitante_sim,
                remates_puerta_local_sim = body.remates_puerta_local_sim,
                remates_puerta_visitante_sim = body.remates_puerta_visitante_sim,
                tarjetas_local_sim = body.tarjetas_local_sim,
                tarjetas_visitante_sim = body.tarjetas_visitante_sim,
                corners_local_sim = body.corners_local_sim,
                corners_visitante_sim = body.corners_visitante_sim,
                confianza = body.confianza,
                resumen_texto = body.resumen_texto,
                fecha_simulacion = body.fecha_simulacion
            )

            // Intentar actualizar supabase
            try {
                supabaseClient.upsertSimulacion(
                    id = id,
                    simulacion = SimulacionDto(
                        goles_local_sim = body.goles_local_sim,
                        goles_visitante_sim = body.goles_visitante_sim,
                        remates_local_sim = body.remates_local_sim,
                        remates_visitante_sim = body.remates_visitante_sim,
                        remates_puerta_local_sim = body.remates_puerta_local_sim,
                        remates_puerta_visitante_sim = body.remates_puerta_visitante_sim,
                        tarjetas_local_sim = body.tarjetas_local_sim,
                        tarjetas_visitante_sim = body.tarjetas_visitante_sim,
                        corners_local_sim = body.corners_local_sim,
                        corners_visitante_sim = body.corners_visitante_sim,
                        confianza = body.confianza,
                        resumen_texto = body.resumen_texto,
                        fecha_simulacion = body.fecha_simulacion
                    )
                )
                dao.marcarSincronizado(id)
            } catch (e: Exception) {
                // Ignore failure for offline first
            }

            return@withContext dao.getPartidoById(id).firstOrNull()?.toDomain() 
                ?: throw IllegalStateException("Partido no encontrado tras simulación")
        } else {
            throw Exception("Error del backend: ${response.code()}")
        }
    }

    override suspend fun sincronizarConSupabase(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val noSincronizados = dao.getPartidosNoSincronizados()
            if (noSincronizados.isNotEmpty()) {
                val dtos = noSincronizados.map { it.toDomain().let { p ->
                    PartidoDto(
                        id = p.id,
                        liga = p.liga,
                        equipo_local = p.equipoLocal,
                        equipo_visitante = p.equipoVisitante,
                        fecha_real = p.fechaReal,
                        fecha_simulacion = p.fechaSimulacion,
                        jornada = p.jornada,
                        temporada = p.temporada,
                        estado = p.estado,
                        goles_local_sim = p.golesLocalSim,
                        goles_visitante_sim = p.golesVisitanteSim,
                        remates_local_sim = p.rematesLocalSim,
                        remates_visitante_sim = p.rematesVisitanteSim,
                        remates_puerta_local_sim = p.rematesPuertaLocalSim,
                        remates_puerta_visitante_sim = p.rematesPuertaVisitanteSim,
                        tarjetas_local_sim = p.tarjetasLocalSim,
                        tarjetas_visitante_sim = p.tarjetasVisitanteSim,
                        tarjetas_rojas_local_sim = p.tarjetasRojasLocalSim,
                        tarjetas_rojas_visitante_sim = p.tarjetasRojasVisitanteSim,
                        corners_local_sim = p.cornersLocalSim,
                        corners_visitante_sim = p.cornersVisitanteSim,
                        goles_local_real = p.golesLocalReal,
                        goles_visitante_real = p.golesVisitanteReal,
                        remates_local_real = p.rematesLocalReal,
                        remates_visitante_real = p.rematesVisitanteReal,
                        remates_puerta_local_real = p.rematesPuertaLocalReal,
                        remates_puerta_visitante_real = p.rematesPuertaVisitanteReal,
                        tarjetas_local_real = p.tarjetasLocalReal,
                        tarjetas_visitante_real = p.tarjetasVisitanteReal,
                        corners_local_real = p.cornersLocalReal,
                        corners_visitante_real = p.cornersVisitanteReal,
                        acertado = p.acertado,
                        margen_error = p.margenError,
                        error_total = p.errorTotal,
                        factores_utilizados = p.factoresUtilizados,
                        datos_insuficientes = p.datosInsuficientes,
                        confianza = p.confianza,
                        resumen_texto = p.resumenTexto,
                        es_simulacion_historica = p.esSimulacionHistorica,
                        simulacion_tipo = p.simulacionTipo
                    )
                } }
                supabaseClient.sincronizarPartidos(dtos)
                noSincronizados.forEach { dao.marcarSincronizado(it.id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun forzarActualizacion(liga: String?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val partidosRemotos = supabaseClient.getProximosPartidos(liga, 50)
            dao.insertOrReplaceAll(partidosRemotos.map { it.toDomain().toEntity() })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEstadoSistema(): EstadoSistemaDto? = withContext(Dispatchers.IO) {
        try {
            supabaseClient.getEstadoSistema()
        } catch (e: Exception) {
            null
        }
    }
}
