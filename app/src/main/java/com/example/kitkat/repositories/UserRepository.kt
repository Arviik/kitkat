package com.example.kitkat.repositories

import android.content.Context
import android.content.SharedPreferences
import com.example.kitkat.app_utils.SHARED_PREF_KEY
import com.example.kitkat.network.ApiClient
import com.example.kitkat.network.dto.LoginRequestDTO
import com.example.kitkat.network.dto.LoginResponseDTO
import com.example.kitkat.network.dto.UserDTO
import com.example.kitkat.network.dto.UserWithoutPasswordDTO
import com.example.kitkat.network.services.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(private val context: Context) { // ✅ Conserve le contexte en attribut

    private val api = ApiClient.retrofit.create(UserService::class.java)
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)

    fun registerUser(userDto: UserDTO, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val call = api.registerUser(userDto)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    loginUser(
                        loginRequestDTO = LoginRequestDTO(email = userDto.email, password = userDto.password),
                        onSuccess = onSuccess,
                        onError = onError
                    )
                } else {
                    onError(Exception("Erreur API: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onError(t)
            }
        })
    }

    fun loginUser(
        loginRequestDTO: LoginRequestDTO,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val call = api.loginUser(loginRequestDTO)
        call.enqueue(object : Callback<LoginResponseDTO> {
            override fun onResponse(call: Call<LoginResponseDTO>, response: Response<LoginResponseDTO>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (token != null) {
                        saveToken(token)
                        fetchUserData(token, onSuccess, onError)
                    } else {
                        onError(Exception("Token null"))
                    }
                } else {
                    onError(Exception("Erreur API: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<LoginResponseDTO>, t: Throwable) {
                onError(t)
            }
        })
    }

    private fun fetchUserData(
        token: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val api = ApiClient.retrofit.create(UserService::class.java)

        api.getCurrentUser("Bearer $token").enqueue(object : Callback<UserWithoutPasswordDTO> {
            override fun onResponse(call: Call<UserWithoutPasswordDTO>, response: Response<UserWithoutPasswordDTO>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        with(sharedPref.edit()) {
                            putInt("USER_ID", user.id ?: -1)
                            putString("USER_NAME", user.name)
                            apply()
                        }
                        onSuccess()
                    } else {
                        onError(Exception("Réponse vide de l'API"))
                    }
                } else {
                    onError(Exception("Erreur lors de la récupération de l'utilisateur: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<UserWithoutPasswordDTO>, t: Throwable) {
                onError(t)
            }
        })
    }

    private fun saveToken(token: String) {
        println("Token: $token")
        sharedPref.edit().putString("AUTH_TOKEN", token).apply()
    }

    fun getToken(): String? {
        return sharedPref.getString("AUTH_TOKEN", null)
    }

    fun logout() {
        sharedPref.edit().remove("AUTH_TOKEN").remove("USER_ID").remove("USER_NAME").apply()
    }
}
