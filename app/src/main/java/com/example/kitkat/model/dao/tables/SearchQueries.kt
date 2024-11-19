package com.example.kitkat.model.dao.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object SearchQueries : IntIdTable() {
    val user = reference("user", Users)
    val query = text("query")
    val createdAt = timestamp("created_at")
}
