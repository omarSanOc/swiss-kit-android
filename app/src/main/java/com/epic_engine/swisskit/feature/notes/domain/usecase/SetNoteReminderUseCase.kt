package com.epic_engine.swisskit.feature.notes.domain.usecase

import com.epic_engine.swisskit.feature.notes.domain.model.Note
import com.epic_engine.swisskit.feature.notes.domain.model.NoteReminderRequest
import com.epic_engine.swisskit.feature.notes.domain.reminder.ReminderScheduler
import com.epic_engine.swisskit.feature.notes.domain.repository.NoteRepository
import javax.inject.Inject

class SetNoteReminderUseCase @Inject constructor(
    private val repo: NoteRepository,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke(note: Note, request: NoteReminderRequest?) {
        val updated = note.copy(
            reminderAt = request?.triggerAtMillis,
            reminderRecurrence = request?.recurrence,
            updatedAt = System.currentTimeMillis()
        )
        repo.save(updated)
        if (request != null) {
            reminderScheduler.schedule(updated.id, updated.title, request)
        } else {
            reminderScheduler.cancel(updated.id)
        }
    }
}
