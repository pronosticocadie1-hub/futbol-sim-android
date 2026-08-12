package com.cadie.futbolsim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cadie.futbolsim.data.local.entities.EquipoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {
    @Query("SELECT * FROM equipos WHERE liga = :liga")
    fun getEquiposByLiga(liga: String): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM equipos WHERE nombre = :nombre LIMIT 1")
    suspend fun getEquipoByNombre(nombre: String): EquipoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(equipo: EquipoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceAll(equipos: List<EquipoEntity>)

    @Query("UPDATE equipos SET elo_puntos = :elo, ultima_actualizacion = :timestamp WHERE nombre = :nombre")
    suspend fun updateElo(nombre: String, elo: Int, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM equipos")
    suspend fun getAllEquipos(): List<EquipoEntity>
}
