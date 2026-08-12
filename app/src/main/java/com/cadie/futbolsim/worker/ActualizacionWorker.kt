package com.cadie.futbolsim.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.cadie.futbolsim.domain.repository.IPartidoRepository
import com.cadie.futbolsim.utils.AppLogger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltWorker
class ActualizacionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val partidoRepository: IPartidoRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            AppLogger.info("Iniciando ActualizacionWorker diario...")
            
            // Forzar actualización general
            partidoRepository.forzarActualizacion(null)
            
            // Sincronizar con base de datos en Supabase
            partidoRepository.sincronizarConSupabase()
            
            AppLogger.info("ActualizacionWorker finalizado con éxito.")
            Result.success()
        } catch (e: Exception) {
            AppLogger.error("Error en ActualizacionWorker", e.message)
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "ActualizacionDiariaWorker"

        fun scheduleDaily(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                
                if (before(currentDate)) {
                    add(Calendar.HOUR_OF_DAY, 24)
                }
            }
            
            val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

            val workRequest = PeriodicWorkRequestBuilder<ActualizacionWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
            AppLogger.info("Actualizacion diaria programada para las 00:00.")
        }
    }
}
