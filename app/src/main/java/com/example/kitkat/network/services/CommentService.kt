package com.example.kitkat.network.services

import com.example.kitkat.network.dto.CommentDTO
import retrofit2.Call
import retrofit2.http.*

interface CommentService {
    @GET("comments/video/{videoId}")
    fun getCommentsByVideo(@Path("videoId") videoId: Int): Call<List<CommentDTO>>

    @POST("comments")
    fun postComment(@Body comment: CommentDTO): Call<CommentDTO>
}
