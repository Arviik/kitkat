package com.example.kitkat.model.dao.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Notifications : IntIdTable() {
    val user = reference("user", Users)
    val type = varchar("type", 255)
    val relatedUser = reference("related_user", Users)
    val video = reference("video", Videos)
    val isRead = bool("is_read")
    val createdAt = timestamp("created_at")
}
