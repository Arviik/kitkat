package com.example.kitkat.network.dto

import java.io.Serializable

data class CommentDTO(
    val id: Int? = null,
    val authorId: Int,
    val videoId: Int,
    val text: String,
    val createdAt: String,
    val likesCount: Int,
    var authorName: String? = null
) : Serializable