package com.example.kitkat.network.dto

import java.io.Serializable

data class Message (
    val senderId: Int,
    val receiverId: Int,
    val content: String,
    val createdAt: String,
    val conversationId: Int,
    val isSystemMessage: Boolean = false
) : Serializable