package com.example.arsisi_frontend.data.remote

import com.example.arsisi_frontend.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    // IP untuk emulator: 10.0.2.2 | IP untuk HP fisik: gunakan IP komputer (misal 192.168.x.x)
    private const val BASE_URL = "http://10.0.2.2:5000/"
    
    fun getApiService(token: String? = null): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val requestHeaders = req.newBuilder()
                .apply {
                    token?.let { 
                        addHeader("Authorization", "Bearer $it")
                    }
                }
                .build()
            chain.proceed(requestHeaders)
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        
        return retrofit.create(ApiService::class.java)
    }
}
