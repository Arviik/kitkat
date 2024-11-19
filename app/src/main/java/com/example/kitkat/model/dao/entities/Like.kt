package com.example.kitkat.model.dao.entities

import com.example.kitkat.model.dao.tables.Likes
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Like(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Like>(Likes)

    var video by Video referencedOn Likes.video
    var user by User referencedOn Likes.user
}
