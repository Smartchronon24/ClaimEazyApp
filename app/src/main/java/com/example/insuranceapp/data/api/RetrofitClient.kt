package com.example.insuranceapp.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import android.content.Context
import android.content.SharedPreferences

object RetrofitClient {
    private var BASE_URL = "https://placeholder.ngrok-free.app" // Will be overridden
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val dynamicBaseUrlInterceptor = Interceptor { chain ->
            var request = chain.request()
            val key = prefs?.getString("ngrok_key", null)
            if (key != null) {
                val newUrl = request.url.newBuilder()
                    .scheme("https")
                    .host("$key.ngrok-free.app")
                    .build()
                request = request.newBuilder()
                    .url(newUrl)
                    .build()
            }
            chain.proceed(request)
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(dynamicBaseUrlInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }

    fun updateNgrokKey(key: String) {
        prefs?.edit()?.putString("ngrok_key", key)?.apply()
    }

    fun getNgrokKey(): String? {
        return prefs?.getString("ngrok_key", null)
    }
}
