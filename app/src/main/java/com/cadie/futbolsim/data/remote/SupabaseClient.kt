package com.cadie.futbolsim.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class PartidoDto(
    val id: String,
    val liga: String,
    val equipo_local: String,
    val equipo_visitante: String,
    val fecha_real: Long,
    val fecha_simulacion: Long?,
    val jornada: Int,
    val temporada: String,
    val estado: String,
    val goles_local_sim: Int?,
    val goles_visitante_sim: Int?,
    val remates_local_sim: Int?,
    val remates_visitante_sim: Int?,
    val remates_puerta_local_sim: Int?,
    val remates_puerta_visitante_sim: Int?,
    val tarjetas_local_sim: Int?,
    val tarjetas_visitante_sim: Int?,
    val tarjetas_rojas_local_sim: Int?,
    val tarjetas_rojas_visitante_sim: Int?,
    val corners_local_sim: Int?,
    val corners_visitante_sim: Int?,
    val goles_local_real: Int?,
    val goles_visitante_real: Int?,
    val remates_local_real: Int?,
    val remates_visitante_real: Int?,
    val remates_puerta_local_real: Int?,
    val remates_puerta_visitante_real: Int?,
    val tarjetas_local_real: Int?,
    val tarjetas_visitante_real: Int?,
    val corners_local_real: Int?,
    val corners_visitante_real: Int?,
    val acertado: Boolean?,
    val margen_error: Double?,
    val error_total: Double?,
    val factores_utilizados: String,
    val datos_insuficientes: Boolean,
    val confianza: Int,
    val resumen_texto: String,
    val es_simulacion_historica: Boolean,
    val simulacion_tipo: String
)

@Serializable
data class SimulacionDto(
    val goles_local_sim: Int?,
    val goles_visitante_sim: Int?,
    val remates_local_sim: Int?,
    val remates_visitante_sim: Int?,
    val remates_puerta_local_sim: Int?,
    val remates_puerta_visitante_sim: Int?,
    val tarjetas_local_sim: Int?,
    val tarjetas_visitante_sim: Int?,
    val corners_local_sim: Int?,
    val corners_visitante_sim: Int?,
    val confianza: Int,
    val resumen_texto: String,
    val fecha_simulacion: Long
)

@Serializable
data class EstadoSistemaDto(
    val id: String,
    val estado_backend: String,
    val ultima_actualizacion: Long
)

@Serializable
data class ResumenJornadaDto(
    val id: String,
    val descripcion: String
)

@Serializable
data class ConfiguracionUsuarioDto(
    val usuario_id: String,
    val notificaciones_activas: Boolean
)

interface ISupabaseClient {
    suspend fun getProximosPartidos(liga: String?, limite: Int): List<PartidoDto>
    suspend fun getPartidoById(id: String): PartidoDto?
    suspend fun upsertPartido(partido: PartidoDto)
    suspend fun upsertSimulacion(id: String, simulacion: SimulacionDto)
    suspend fun getEstadoSistema(): EstadoSistemaDto?
    suspend fun getResumenEntrenamiento(): List<ResumenJornadaDto>
    suspend fun getConfiguracionUsuario(): ConfiguracionUsuarioDto?
    suspend fun upsertConfiguracionUsuario(config: ConfiguracionUsuarioDto)
    suspend fun sincronizarPartidos(partidos: List<PartidoDto>)
}

@Singleton
class AppSupabaseClient @Inject constructor(
    private val supabaseUrl: String,
    private val supabaseKey: String
) : ISupabaseClient {

    private val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey
    ) {
        install(Postgrest)
        install(GoTrue)
    }

    override suspend fun getProximosPartidos(liga: String?, limite: Int): List<PartidoDto> {
        return client.postgrest["partidos"]
            .select {
                if (liga != null) {
                    filter {
                        eq("liga", liga)
                    }
                }
                filter {
                    eq("estado", "programado")
                }
                limit(limite.toLong())
            }.decodeList<PartidoDto>()
    }

    override suspend fun getPartidoById(id: String): PartidoDto? {
        return client.postgrest["partidos"]
            .select {
                filter {
                    eq("id", id)
                }
            }.decodeSingleOrNull<PartidoDto>()
    }

    override suspend fun upsertPartido(partido: PartidoDto) {
        client.postgrest["partidos"].upsert(partido)
    }

    override suspend fun upsertSimulacion(id: String, simulacion: SimulacionDto) {
        client.postgrest["partidos"].update(simulacion) {
            filter {
                eq("id", id)
            }
        }
    }

    override suspend fun getEstadoSistema(): EstadoSistemaDto? {
        return client.postgrest["estado_sistema"]
            .select()
            .decodeSingleOrNull<EstadoSistemaDto>()
    }

    override suspend fun getResumenEntrenamiento(): List<ResumenJornadaDto> {
        return client.postgrest["resumen_jornada"]
            .select()
            .decodeList<ResumenJornadaDto>()
    }

    override suspend fun getConfiguracionUsuario(): ConfiguracionUsuarioDto? {
        return client.postgrest["config_usuario"]
            .select()
            .decodeSingleOrNull<ConfiguracionUsuarioDto>()
    }

    override suspend fun upsertConfiguracionUsuario(config: ConfiguracionUsuarioDto) {
        client.postgrest["config_usuario"].upsert(config)
    }

    override suspend fun sincronizarPartidos(partidos: List<PartidoDto>) {
        client.postgrest["partidos"].upsert(partidos)
    }
}
