package com.example.arsisi_frontend.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.arsisi_frontend.data.remote.ApiService

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:5000/api/")  // Ganti URL PHPMyAdmin
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ApiService = retrofit.create(ApiService::class.java)
}
