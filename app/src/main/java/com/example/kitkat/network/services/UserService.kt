package com.example.kitkat.network.services

import com.example.kitkat.network.dto.UserDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("auth/register")
    fun registerUser(@Body userDto: UserDTO): Call<Unit>
}
