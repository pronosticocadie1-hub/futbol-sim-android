# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Mantener la clase Application
-keep class com.cadie.futbolsim.FutbolSimApp { *; }

# Mantener entidades Room
-keep class com.cadie.futbolsim.data.local.entities.** { *; }

# Mantener modelos de dominio (necesarios para serialización JSON)
-keep class com.cadie.futbolsim.domain.models.** { *; }

# Mantener data classes de Retrofit
-keep class com.cadie.futbolsim.data.remote.** { *; }

# Hilt - mantener todos los módulos y componentes
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.AndroidEntryPoint *;
}

# WorkManager
-keep class androidx.work.** { *; }
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# Supabase / Ktor
-keep class io.ktor.** { *; }
-keep class io.github.jan.** { *; }
-dontwarn io.ktor.**

# Kotlinx Serialization
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class * {
    @kotlinx.serialization.SerialName *;
}

# Retrofit + OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Coil
-keep class coil.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn android.arch.persistence.room.**

# Gson
-keepattributes Signature
-keepattributes EnclosingMethod
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Security Crypto
-keep class androidx.security.crypto.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
