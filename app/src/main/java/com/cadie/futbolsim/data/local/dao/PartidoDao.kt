package com.cadie.futbolsim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cadie.futbolsim.data.local.entities.PartidoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidoDao {
    @Query("SELECT * FROM partidos WHERE liga = :liga AND estado = 'programado' ORDER BY fecha_real ASC LIMIT :limite")
    fun getProximosPartidos(liga: String, limite: Int): Flow<List<PartidoEntity>>

    @Query("SELECT * FROM partidos WHERE estado = 'programado' ORDER BY fecha_real ASC LIMIT :limite")
    fun getProximosPartidosTodas(limite: Int): Flow<List<PartidoEntity>>

    @Query("SELECT * FROM partidos WHERE id = :id")
    fun getPartidoById(id: String): Flow<PartidoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(partido: PartidoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceAll(partidos: List<PartidoEntity>)

    @Query("""
        UPDATE partidos SET 
            goles_local_sim = :goles_local_sim,
            goles_visitante_sim = :goles_visitante_sim,
            remates_local_sim = :remates_local_sim,
            remates_visitante_sim = :remates_visitante_sim,
            remates_puerta_local_sim = :remates_puerta_local_sim,
            remates_puerta_visitante_sim = :remates_puerta_visitante_sim,
            tarjetas_local_sim = :tarjetas_local_sim,
            tarjetas_visitante_sim = :tarjetas_visitante_sim,
            corners_local_sim = :corners_local_sim,
            corners_visitante_sim = :corners_visitante_sim,
            confianza = :confianza,
            resumen_texto = :resumen_texto,
            fecha_simulacion = :fecha_simulacion,
            sincronizado = 0
        WHERE id = :id
    """)
    suspend fun updateSimulacion(
        id: String,
        goles_local_sim: Int?,
        goles_visitante_sim: Int?,
        remates_local_sim: Int?,
        remates_visitante_sim: Int?,
        remates_puerta_local_sim: Int?,
        remates_puerta_visitante_sim: Int?,
        tarjetas_local_sim: Int?,
        tarjetas_visitante_sim: Int?,
        corners_local_sim: Int?,
        corners_visitante_sim: Int?,
        confianza: Int,
        resumen_texto: String,
        fecha_simulacion: Long
    )

    @Query("SELECT * FROM partidos WHERE sincronizado = 0")
    suspend fun getPartidosNoSincronizados(): List<PartidoEntity>

    @Query("UPDATE partidos SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizado(id: String)

    @Query("SELECT COUNT(*) FROM partidos")
    suspend fun contarPartidos(): Int

    @Query("DELETE FROM partidos WHERE id IN (SELECT id FROM partidos ORDER BY fecha_real ASC LIMIT :limite)")
    suspend fun eliminarPartidosAntiguos(limite: Int = 1000)

    @Query("SELECT * FROM partidos WHERE temporada = :temporada")
    suspend fun getPartidosPorTemporada(temporada: String): List<PartidoEntity>
}
