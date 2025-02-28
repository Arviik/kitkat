package com.example.kitkat.network

import com.example.kitkat.network.services.VideoService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/" // Adresse locale pour l'émulateur Android

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val videoService: VideoService by lazy { retrofit.create(VideoService::class.java) }
}