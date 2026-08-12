package com.cadie.futbolsim.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cadie.futbolsim.data.local.dao.EquipoDao
import com.cadie.futbolsim.data.local.dao.PartidoDao
import com.cadie.futbolsim.data.local.entities.EquipoEntity
import com.cadie.futbolsim.data.local.entities.PartidoEntity

@Database(entities = [PartidoEntity::class, EquipoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun partidoDao(): PartidoDao
    abstract fun equipoDao(): EquipoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "futbolsim_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
