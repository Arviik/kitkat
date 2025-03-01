package com.example.kitkat.repositories

import com.example.kitkat.network.dto.Conversation
import com.example.kitkat.network.ApiClient
import com.example.kitkat.network.services.ConversationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConversationRepository {
    private val api = ApiClient.retrofit.create(ConversationService::class.java)

    fun getConversationsByUser(userId: Int, onSuccess: (List<Conversation>) -> Unit, onError: (Throwable) -> Unit) {
        val call = api.getConversationsByUser(userId)
        call.enqueue(object : Callback<List<Conversation>> {
            override fun onResponse(call: Call<List<Conversation>>, response: Response<List<Conversation>>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) } ?: onError(Exception("No data"))
                } else {
                    onError(Exception("API error: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<List<Conversation>>, t: Throwable) {
                onError(t)
            }
        })
    }

    fun createConversation(
        conversation: Conversation,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val call = api.createConversation(conversation)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(Exception("API error: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onError(t)
            }
        })
    }
}
