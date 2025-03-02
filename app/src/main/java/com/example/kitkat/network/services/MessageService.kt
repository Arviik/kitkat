package com.example.kitkat.network.services

import com.example.kitkat.network.dto.Conversation
import com.example.kitkat.network.dto.Message
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageService {
    @GET("/messages/conversation/{id}")
    fun getMessagesByConversation(@Path("id") conversationId: Int): Call<List<Message>>

    @POST("/messages/conversation/{id}")
    fun sendMessageToConversation(@Path("id") message: Message): Call<List<Message>>
}