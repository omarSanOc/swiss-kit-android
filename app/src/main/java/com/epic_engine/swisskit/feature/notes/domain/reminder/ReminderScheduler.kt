package com.epic_engine.swisskit.feature.notes.domain.reminder

import com.epic_engine.swisskit.feature.notes.domain.model.NoteReminderRequest

interface ReminderScheduler {
    fun schedule(noteId: String, title: String, request: NoteReminderRequest)
    fun cancel(noteId: String)
}
