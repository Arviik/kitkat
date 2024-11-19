package com.example.kitkat.model.dao.entities

import com.example.kitkat.model.dao.tables.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name by Users.name
    var email by Users.email
    var passwordHash by Users.passwordHash
    var profilePictureUrl by Users.profilePictureUrl
    var bio by Users.bio
    var followers by User referencedOn Users.followers
    var followersCount by Users.followersCount
    var followingCount by Users.followingCount
}
