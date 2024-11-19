package com.example.kitkat.model.dao.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Videos : IntIdTable() {
    val title = varchar("title", 255)
    val duration = integer("duration")
    val author = reference("user", Users)
    val videoUrl = text("video_url")
    val viewCount = integer("view_count")
    val likeCount = integer("like_count")
    val commentCount = integer("comment_count")
    val createdAt = timestamp("created_at")
    val isPublic = bool("is_public")
    val sound = reference("sound", Sounds)
}
