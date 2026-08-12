package com.cadie.futbolsim.di

import android.content.Context
import android.content.SharedPreferences
import com.cadie.futbolsim.data.local.AppDatabase
import com.cadie.futbolsim.data.local.dao.EquipoDao
import com.cadie.futbolsim.data.local.dao.PartidoDao
import com.cadie.futbolsim.data.remote.AppSupabaseClient
import com.cadie.futbolsim.data.remote.BackendApi
import com.cadie.futbolsim.data.remote.ISupabaseClient
import com.cadie.futbolsim.data.repository.IPartidoRepository
import com.cadie.futbolsim.data.repository.PartidoRepositoryImpl
import com.cadie.futbolsim.domain.usecases.GetProximosPartidosUseCase
import com.cadie.futbolsim.domain.usecases.SimularPartidoUseCase
import com.cadie.futbolsim.utils.EncryptedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ─── Utilidades ───────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideEncryptedPrefs(@ApplicationContext context: Context): EncryptedPrefs {
        return EncryptedPrefs(context)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("futbolsim_normal_prefs", Context.MODE_PRIVATE)
    }

    // ─── Base de Datos Room ────────────────────────────────────────

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun providePartidoDao(db: AppDatabase): PartidoDao {
        return db.partidoDao()
    }

    @Provides
    @Singleton
    fun provideEquipoDao(db: AppDatabase): EquipoDao {
        return db.equipoDao()
    }

    // ─── Cliente Supabase ──────────────────────────────────────────

    @Provides
    @Singleton
    fun provideSupabaseClient(prefs: EncryptedPrefs): ISupabaseClient {
        val url = prefs.getSupabaseUrl().ifEmpty {
            // Valor de desarrollo por defecto (se sobreescribe en Setup)
            "https://placeholder.supabase.co"
        }
        val key = prefs.getSupabaseKey().ifEmpty {
            "placeholder-anon-key"
        }
        return AppSupabaseClient(supabaseUrl = url, supabaseKey = key)
    }

    // ─── OkHttp + Retrofit (Backend FastAPI) ──────────────────────

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        prefs: EncryptedPrefs
    ): Retrofit {
        val baseUrl = prefs.getBackendUrl().let { url ->
            if (url.isNotEmpty()) {
                if (url.endsWith("/")) url else "$url/"
            } else {
                // URL de desarrollo por defecto (se sobreescribe en Setup)
                "https://football-sim-backend.onrender.com/"
            }
        }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBackendApi(retrofit: Retrofit): BackendApi {
        return retrofit.create(BackendApi::class.java)
    }

    // ─── Repositorio ──────────────────────────────────────────────

    @Provides
    @Singleton
    fun providePartidoRepository(
        dao: PartidoDao,
        supabaseClient: ISupabaseClient,
        backendApi: BackendApi,
        prefs: SharedPreferences
    ): IPartidoRepository {
        return PartidoRepositoryImpl(
            dao = dao,
            supabaseClient = supabaseClient,
            backendApi = backendApi,
            prefs = prefs
        )
    }

    // ─── Use Cases ────────────────────────────────────────────────

    @Provides
    fun provideGetProximosPartidosUseCase(repo: IPartidoRepository): GetProximosPartidosUseCase {
        return GetProximosPartidosUseCase(repo)
    }

    @Provides
    fun provideSimularPartidoUseCase(repo: IPartidoRepository): SimularPartidoUseCase {
        return SimularPartidoUseCase(repo)
    }
}
