package com.cadie.futbolsim.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "futbolsim_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val normalPreferences: SharedPreferences = context.getSharedPreferences(
        "futbolsim_normal_prefs",
        Context.MODE_PRIVATE
    )

    fun saveApiKey(keyName: String, value: String) {
        sharedPreferences.edit().putString("API_KEY_$keyName", value).apply()
    }

    fun getApiKey(keyName: String): String {
        return sharedPreferences.getString("API_KEY_$keyName", "") ?: ""
    }

    fun saveSupabaseUrl(url: String) {
        sharedPreferences.edit().putString("SUPABASE_URL", url).apply()
    }

    fun getSupabaseUrl(): String {
        return sharedPreferences.getString("SUPABASE_URL", "") ?: ""
    }

    fun saveSupabaseKey(key: String) {
        sharedPreferences.edit().putString("SUPABASE_KEY", key).apply()
    }

    fun getSupabaseKey(): String {
        return sharedPreferences.getString("SUPABASE_KEY", "") ?: ""
    }

    fun saveBackendUrl(url: String) {
        sharedPreferences.edit().putString("BACKEND_URL", url).apply()
    }

    fun getBackendUrl(): String {
        return sharedPreferences.getString("BACKEND_URL", "") ?: ""
    }

    fun isFirstRun(): Boolean {
        return normalPreferences.getBoolean("FIRST_RUN", true)
    }

    fun setFirstRun(isFirst: Boolean) {
        normalPreferences.edit().putBoolean("FIRST_RUN", isFirst).apply()
    }

    fun isLoggedIn(): Boolean {
        return normalPreferences.getBoolean("IS_LOGGED_IN", false)
    }

    fun setLoggedIn(logged: Boolean) {
        normalPreferences.edit().putBoolean("IS_LOGGED_IN", logged).apply()
    }

    fun logout() {
        setLoggedIn(false)
    }

    fun hasApiKeys(): Boolean {
        val url = getSupabaseUrl()
        val key = getSupabaseKey()
        return url.isNotEmpty() && key.isNotEmpty()
    }

    fun getAllKeys(): Map<String, String> {
        val allEntries = sharedPreferences.all
        val maskedKeys = mutableMapOf<String, String>()
        
        for ((key, value) in allEntries) {
            if (value is String && value.isNotEmpty()) {
                val maskedValue = if (value.length > 8) {
                    value.take(4) + "..." + value.takeLast(4)
                } else {
                    "***"
                }
                maskedKeys[key] = maskedValue
            }
        }
        return maskedKeys
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
        normalPreferences.edit().clear().apply()
    }
}
