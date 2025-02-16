package com.example.kitkat.network.services

import com.example.kitkat.api.models.dataclass.VideoDTO
import retrofit2.Call
import com.example.kitkat.api.models.dataclass.VideoWithAuthor
import retrofit2.http.GET
import retrofit2.http.Path

interface VideoService {
    @GET("videos-with-authors")
    fun getVideosWithAuthors(): Call<List<VideoWithAuthor>>

    @GET("/videos/author/{authorId}")
    fun getVideosByAuthor(@Path("authorId") authorId: Int): Call<List<VideoDTO>>
}