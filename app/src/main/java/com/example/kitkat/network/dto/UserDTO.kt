package com.example.kitkat.api.models.dataclass
import java.io.Serializable

data class UserDTO(
    val id: Int? = null,
    val name: String,
    val email: String,
    val passwordHash: String,
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    val followersCount: Int = 0, // valeur par défaut
    val followingCount: Int = 0  // valeur par défaut
)

data class UserWithoutPasswordDTO(
    val id: Int? = null,
    val name: String,
    val email: String,
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0
) : Serializable


data class LoginRequestDTO(
    val email: String,
    val password: String
)