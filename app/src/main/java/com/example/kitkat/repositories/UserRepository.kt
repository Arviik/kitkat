package com.example.kitkat.repositories

import android.content.SharedPreferences
import com.example.kitkat.network.ApiClient
import com.example.kitkat.network.dto.LoginRequestDTO
import com.example.kitkat.network.dto.LoginResponseDTO
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

        fun loginUser(loginRequestDTO: LoginRequestDTO, onSuccess: (LoginResponseDTO) -> Unit, onError: (Throwable) -> Unit) {
            val call = api.loginUser(loginRequestDTO)
            call.enqueue(object : Callback<LoginResponseDTO> {
                override fun onResponse(call: Call<LoginResponseDTO>, response: Response<LoginResponseDTO>) {
                    if (response.isSuccessful) {
                        println("loginUser response is successful")
                        val token = response.body()?.token
                        println("Token : $token")

//                        val sharedPref = activity?.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
//                        with(sharedPref.edit()) {
//                            putString("AUTH_TOKEN", token)
//                            apply()
//                        }
                    } else {
                        onError(Exception("API error: ${response.code()}"))
                    }
                }

                override fun onFailure(call: Call<LoginResponseDTO>, t: Throwable) {
                    onError(t)
                }
            })
        }
    }
}
