package com.example.kitkat.model.dao

import android.util.Log
import com.example.kitkat.model.dao.tables.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseHelper {
    companion object {
        fun initDatabase() {
            try {
                transaction(DbSettings.db) {
                    addLogger(StdOutSqlLogger)
                    SchemaUtils.create(
                        Comments, Followers, Likes, Notifications, SearchQueries, Sounds, Users, Videos
                    )
                }
            } catch (e: Exception) {
                Log.e("DatabaseConnection", "Failed to initialize the database", e)
            }
        }
    }
}
