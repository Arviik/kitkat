package com.example.kitkat.api.models.dataclass

data class Conversation(
    val id: Int,
    val participants: List<Int>, // IDs des utilisateurs participants à la conversation
    val createdAt: String,
    val lastMessage: String?,
    val username: String?
)
