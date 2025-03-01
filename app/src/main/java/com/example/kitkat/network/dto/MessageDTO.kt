package com.example.kitkat.network.dto

data class Message (
    val senderId: Int,
    val receiverId: Int,
    val content: String,
    val createdAt: String,
    val conversationId: Int,
    val isSystemMessage: Boolean = false
)