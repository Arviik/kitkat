package com.example.kitkat.repositories

import com.example.kitkat.network.ApiClient
import com.example.kitkat.network.dto.Message
import com.example.kitkat.network.services.ConversationService
import com.example.kitkat.network.services.MessageService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageRepository {

    private val api = ApiClient.retrofit.create(MessageService::class.java)

    fun getMessagesByConversation(
        conversationId: Int,
        onSuccess: (List<Message>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val call = api.getMessagesByConversation(conversationId)
        call.enqueue(object : Callback<List<Message>> {
            override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) } ?: onError(Exception("No data"))
                } else {
                    onError(Exception("API error: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                onError(t)
            }
        })
    }

    fun sendMessageToConversation(
        message: Message,
        onSuccess: (List<Message>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val call = api.sendMessageToConversation(message)
        call.enqueue(object : Callback<List<Message>> {
            override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) } ?: onError(Exception("No data"))
                } else {
                    onError(Exception("API error: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                onError(t)
            }
        })
    }

}