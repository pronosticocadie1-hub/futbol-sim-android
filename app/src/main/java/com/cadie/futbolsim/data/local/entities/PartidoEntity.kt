package com.cadie.futbolsim.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "partidos",
    indices = [
        Index("liga"),
        Index("temporada"),
        Index("estado")
    ]
)
data class PartidoEntity(
    @PrimaryKey
    val id: String,
    val liga: String,
    val equipo_local: String,
    val equipo_visitante: String,
    val fecha_real: Long,
    val fecha_simulacion: Long?,
    val jornada: Int,
    val temporada: String,
    val estado: String,
    
    // Simulación
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
    
    // Reales
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
    
    // Análisis
    val acertado: Boolean?,
    val margen_error: Double?,
    val error_total: Double?,
    val factores_utilizados: String = "{}",
    val datos_insuficientes: Boolean = false,
    val confianza: Int = 50,
    val resumen_texto: String = "",
    
    // Meta
    val sincronizado: Boolean = false,
    val fecha_creacion: Long = System.currentTimeMillis(),
    val es_simulacion_historica: Boolean = false,
    val simulacion_tipo: String = "normal"
)
