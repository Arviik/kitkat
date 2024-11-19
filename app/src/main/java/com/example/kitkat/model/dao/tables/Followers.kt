package com.example.kitkat.model.dao.tables

import org.jetbrains.exposed.dao.id.CompositeIdTable

object Followers : CompositeIdTable() {
    val follower = reference("follower", Users)
    val followed = reference("followed", Users)

    init {
        addIdColumn(follower)
        addIdColumn(followed)
    }

    override val primaryKey = PrimaryKey(follower, followed)
}
