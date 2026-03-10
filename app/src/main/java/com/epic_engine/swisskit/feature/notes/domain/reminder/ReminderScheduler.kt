package com.epic_engine.swisskit.feature.notes.domain.reminder

interface ReminderScheduler {
    fun schedule(noteId: String, title: String, triggerAtMillis: Long)
    fun cancel(noteId: String)
}
