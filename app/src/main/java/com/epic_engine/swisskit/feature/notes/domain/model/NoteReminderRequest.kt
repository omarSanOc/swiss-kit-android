package com.epic_engine.swisskit.feature.notes.domain.model

data class NoteReminderRequest(
    val triggerAtMillis: Long,
    val recurrence: NoteReminderRecurrence
)
