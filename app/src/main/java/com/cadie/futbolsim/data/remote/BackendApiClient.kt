package com.cadie.futbolsim.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class EstadoSistemaResponse(
    val status: String,
    val message: String
)

data class ResumenEntrenamientoResponse(
    val jornadas: List<String>
)

data class SimularRequest(
    val partido_id: String
)

data class SimulacionResponse(
    val id: String,
    val goles_local_sim: Int,
    val goles_visitante_sim: Int,
    val remates_local_sim: Int,
    val remates_visitante_sim: Int,
    val remates_puerta_local_sim: Int,
    val remates_puerta_visitante_sim: Int,
    val tarjetas_local_sim: Int,
    val tarjetas_visitante_sim: Int,
    val corners_local_sim: Int,
    val corners_visitante_sim: Int,
    val confianza: Int,
    val resumen_texto: String,
    val fecha_simulacion: Long
)

data class HealthResponse(
    val status: String
)

interface BackendApi {
    @GET("estado")
    suspend fun getEstadoSistema(): Response<EstadoSistemaResponse>

    @GET("resumen")
    suspend fun getResumenEntrenamiento(): Response<ResumenEntrenamientoResponse>

    @POST("simular")
    suspend fun simularPartido(@Body request: SimularRequest): Response<SimulacionResponse>

    @GET("health")
    suspend fun getHealth(): Response<HealthResponse>
}
