package com.example.kitkat.api.models.dataclass



data class VideoDTO(
    val id: Int? = null,
    val title: String,
    val duration: Int,
    val authorId: Int,
    val videoUrl: String,
    val viewCount: Int = 0,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val createdAt: String,
    val isPublic: Boolean,
)

data class VideoWithAuthor(
    val first: VideoDTO,
    val second: UserWithoutPasswordDTO
)
