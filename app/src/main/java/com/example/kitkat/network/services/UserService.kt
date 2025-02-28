package com.example.kitkat.network.services

import com.example.kitkat.network.dto.LoginRequestDTO
import com.example.kitkat.network.dto.LoginResponseDTO
import com.example.kitkat.network.dto.UserDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("auth/register")
    fun registerUser(@Body userDto: UserDTO): Call<Unit>

    @POST("auth/login")
    fun loginUser(@Body loginRequestDTO: LoginRequestDTO): Call<LoginResponseDTO>
}
