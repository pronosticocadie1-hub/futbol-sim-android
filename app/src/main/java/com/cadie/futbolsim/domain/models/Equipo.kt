package com.cadie.futbolsim.domain.models

import com.cadie.futbolsim.data.local.entities.EquipoEntity

data class Equipo(
    val id: String,
    val nombre: String,
    val liga: String,
    val escudoUrl: String,
    val eloPuntos: Int,
    val rendimientoReciente: String,
    val xgPromedio: Double,
    val xgEncajado: Double,
    val rematesPromedio: Double,
    val cornersPromedio: Double,
    val tarjetasPromedio: Double,
    val lesionados: String,
    val sancionados: String,
    val ultimaActualizacion: Long
)

fun EquipoEntity.toDomain() = Equipo(
    id = id,
    nombre = nombre,
    liga = liga,
    escudoUrl = escudo_url,
    eloPuntos = elo_puntos,
    rendimientoReciente = rendimiento_reciente,
    xgPromedio = xg_promedio,
    xgEncajado = xg_encajado,
    rematesPromedio = remates_promedio,
    cornersPromedio = corners_promedio,
    tarjetasPromedio = tarjetas_promedio,
    lesionados = lesionados,
    sancionados = sancionados,
    ultimaActualizacion = ultima_actualizacion
)

fun Equipo.toEntity() = EquipoEntity(
    id = id,
    nombre = nombre,
    liga = liga,
    escudo_url = escudoUrl,
    elo_puntos = eloPuntos,
    rendimiento_reciente = rendimientoReciente,
    xg_promedio = xgPromedio,
    xg_encajado = xgEncajado,
    remates_promedio = rematesPromedio,
    corners_promedio = cornersPromedio,
    tarjetas_promedio = tarjetasPromedio,
    lesionados = lesionados,
    sancionados = sancionados,
    ultima_actualizacion = ultimaActualizacion
)
