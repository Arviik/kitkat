package com.example.kitkat.model

import java.io.Serializable

data class CommentItem(
    val id: Int? = null,
    val authorName: String,
    val text: String,
    val createdAt: String,
    val likesCount: Int
) : Serializable