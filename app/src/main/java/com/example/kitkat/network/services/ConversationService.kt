package com.example.kitkat.network.services

import com.example.kitkat.network.dto.Conversation
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ConversationService {

    @GET("/conversations/user/{userId}")
    fun getConversationsByUser(@Path("userId") userId: Int): Call<List<Conversation>>

    @POST("/conversations")
    fun createConversation(@Body conversation: Conversation): Call<Void>
}
