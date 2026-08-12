package com.cadie.futbolsim.utils

import android.util.Log

object AppLogger {
    private const val TAG = "FutbolSim"

    fun log(nivel: String, mensaje: String, detalle: String? = null) {
        val fullMessage = if (detalle != null) "$mensaje - Detalle: $detalle" else mensaje
        
        when (nivel.uppercase()) {
            "INFO" -> Log.i(TAG, fullMessage)
            "WARNING" -> Log.w(TAG, fullMessage)
            "ERROR" -> Log.e(TAG, fullMessage)
            else -> Log.d(TAG, fullMessage)
        }
    }

    fun info(mensaje: String, detalle: String? = null) {
        log("INFO", mensaje, detalle)
    }

    fun warning(mensaje: String, detalle: String? = null) {
        log("WARNING", mensaje, detalle)
    }

    fun error(mensaje: String, detalle: String? = null) {
        log("ERROR", mensaje, detalle)
    }
}
