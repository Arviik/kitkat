package com.example.kitkat.model.dao.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val name = varchar("name", 255)
    val email = varchar("email", 255).index()
    val passwordHash = text("password_hash")
    val profilePictureUrl = text("profile_picture_url")
    val bio = text("bio")
    val followers = reference("followers", Users)
    val followersCount = integer("followers_count")
    val followingCount = integer("following_count")
}
