package com.example.kitkat.network.services

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface VideoUploadService {
    @Multipart
    @POST("/upload-video")
    fun uploadVideo(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part video: MultipartBody.Part
    ): Call<ResponseBody>
}
