package com.example.kitkat.network.services

import com.example.kitkat.network.dto.LoginRequestDTO
import com.example.kitkat.network.dto.LoginResponseDTO
import com.example.kitkat.network.dto.UserDTO
import com.example.kitkat.network.dto.UserWithoutPasswordDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {
    @POST("auth/register")
    fun registerUser(@Body userDto: UserDTO): Call<Unit>

    @POST("auth/login")
    fun loginUser(@Body loginRequestDTO: LoginRequestDTO): Call<LoginResponseDTO>

    @GET("auth/me")
    fun getCurrentUser(@Header("Authorization") token: String): Call<UserWithoutPasswordDTO>

    @GET("users/{id}/is-following/{followerId}")
    fun isFollowingUser(
        @Path("id") userId: Int,
        @Path("followerId") followerId: Int
    ): Call<Map<String, Boolean>>

    @POST("users/{id}/follow")
    fun followUser(@Path("id") userId: Int, @Body followerId: Int): Call<Unit>

    @POST("users/{id}/unfollow")
    fun unfollowUser(@Path("id") userId: Int, @Body followerId: Int): Call<Unit>
}
