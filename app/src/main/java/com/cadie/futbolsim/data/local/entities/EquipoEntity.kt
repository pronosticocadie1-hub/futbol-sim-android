package com.cadie.futbolsim.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val liga: String,
    val escudo_url: String = "",
    val elo_puntos: Int = 1500,
    val rendimiento_reciente: String = "[]",
    val xg_promedio: Double = 1.2,
    val xg_encajado: Double = 1.2,
    val remates_promedio: Double = 12.0,
    val corners_promedio: Double = 5.0,
    val tarjetas_promedio: Double = 2.5,
    val lesionados: String = "[]",
    val sancionados: String = "[]",
    val ultima_actualizacion: Long = System.currentTimeMillis()
)
