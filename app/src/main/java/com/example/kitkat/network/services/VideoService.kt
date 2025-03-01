package com.example.kitkat.network.services

import retrofit2.Call
import com.example.kitkat.network.dto.VideoWithAuthor
import retrofit2.http.GET

interface VideoService {
    @GET("videos-with-authors")
    fun getVideosWithAuthors(): Call<List<VideoWithAuthor>>


}