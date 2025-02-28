package com.example.kitkat.repositories

import com.example.kitkat.network.ApiClient
import com.example.kitkat.network.dto.UserDTO
import com.example.kitkat.network.services.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {
    companion object {
        private val api = ApiClient.retrofit.create(UserService::class.java)

        fun registerUser(userDto: UserDTO, onSuccess: (Boolean) -> Unit, onError: (Throwable) -> Unit) {
            val call = api.registerUser(userDto)
            call.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        response.body()?.let { onSuccess(true) }
                    } else {
                        onError(Exception("API error: ${response.code()}"))
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onError(t)
                }
            })
        }
    }
}
