package com.example.kitkat.model.dao.entities

import com.example.kitkat.model.dao.tables.Videos
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Video(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Video>(Videos)

    var title by Videos.title
    var duration by Videos.duration
    var author by User referencedOn Videos.author
    var videoUrl by Videos.videoUrl
    var viewCount by Videos.viewCount
    var likeCount by Videos.likeCount
    var commentCount by Videos.commentCount
    var createdAt by Videos.createdAt
    var isPublic by Videos.isPublic
    var sound by Sound referencedOn Videos.sound
}
