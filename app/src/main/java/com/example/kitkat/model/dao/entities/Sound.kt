package com.example.kitkat.model.dao.entities

import com.example.kitkat.model.dao.tables.Sounds
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Sound(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Sound>(Sounds)

    var title by Sounds.title
    var author by User referencedOn Sounds.author
    var duration by Sounds.duration
    var url by Sounds.url
    var usageCount by Sounds.usageCount
}
