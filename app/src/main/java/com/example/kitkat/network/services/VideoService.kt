package com.example.kitkat.network.services

import com.example.kitkat.network.dto.VideoDTO
import retrofit2.Call
import com.example.kitkat.network.dto.VideoWithAuthor
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface VideoService {
    @GET("videos-with-authors")
    fun getVideosWithAuthors(): Call<List<VideoWithAuthor>>

    @GET("/videos/author/{authorId}")
    fun getVideosByAuthor(@Path("authorId") authorId: Int): Call<List<VideoDTO>>

    @POST("videos")
    fun postVideo(@Body video: VideoDTO): Call<VideoDTO>

    @GET("videos/friends")
    fun getFriendsVideos(@Header("Authorization") token: String): Call<List<VideoWithAuthor>>



}