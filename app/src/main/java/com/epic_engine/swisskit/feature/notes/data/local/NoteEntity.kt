package com.epic_engine.swisskit.feature.notes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val reminderAt: Long? = null,
    val reminderRecurrence: String? = null
)
