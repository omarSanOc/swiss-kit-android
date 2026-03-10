package com.epic_engine.swisskit.feature.notes.data.mapper

import com.epic_engine.swisskit.feature.notes.data.local.NoteEntity
import com.epic_engine.swisskit.feature.notes.domain.model.Note

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    reminderAt = reminderAt
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    reminderAt = reminderAt
)
