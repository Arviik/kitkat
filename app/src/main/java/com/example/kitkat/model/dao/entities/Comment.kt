package com.example.kitkat.model.dao.entities

import com.example.kitkat.model.dao.tables.Comments
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Comment(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Comment>(Comments)

    var author by User referencedOn Comments.author
    var video by Video referencedOn Comments.video
    var text by Comments.text
    var createdAt by Comments.createdAt
    var likesCount by Comments.likesCount
}
