package com.example.kitkat.model.dao.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Comments : IntIdTable() {
    val author = reference("user", Users)
    val video = reference("video", Videos)
    val text = text("text")
    val createdAt = timestamp("created_at")
    val likesCount = integer("likes_count")
}
