package com.example.kitkat.repositories

import com.example.kitkat.network.dto.VideoWithAuthor
import com.example.kitkat.network.ApiClient
import com.example.kitkat.network.services.VideoService

class VideoRepository {
    private val api = ApiClient.retrofit.create(VideoService::class.java)

    fun getVideosWithAuthors(onSuccess: (List<VideoWithAuthor>) -> Unit, onError: (Throwable) -> Unit) {
        val call = api.getVideosWithAuthors()
        call.enqueue(object : retrofit2.Callback<List<VideoWithAuthor>> {
            override fun onResponse(call: retrofit2.Call<List<VideoWithAuthor>>, response: retrofit2.Response<List<VideoWithAuthor>>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError(Exception("API error: ${response.code()}"))
                }
            }

            override fun onFailure(call: retrofit2.Call<List<VideoWithAuthor>>, t: Throwable) {
                onError(t)
            }
        })
    }
}
