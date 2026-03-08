package com.epic_engine.swisskit.core.database

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

@Database(
    entities = [DatabasePlaceholderEntity::class],
    version = 1,
    exportSchema = true
)
abstract class SwissKitDatabase : RoomDatabase()

@Entity(tableName = "database_placeholder")
internal data class DatabasePlaceholderEntity(
    @PrimaryKey val id: Int = 0
)
