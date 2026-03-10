package com.epic_engine.swisskit.feature.notes.domain.model

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val reminderAt: Long? = null
)
