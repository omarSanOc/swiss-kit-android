package com.epic_engine.swisskit.feature.notes.domain.usecase

import com.epic_engine.swisskit.feature.notes.domain.model.Note
import com.epic_engine.swisskit.feature.notes.domain.repository.NoteRepository
import java.util.UUID
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(private val repo: NoteRepository) {
    suspend operator fun invoke(note: Note): Note {
        val now = System.currentTimeMillis()
        val toSave = if (note.id.isBlank()) {
            note.copy(id = UUID.randomUUID().toString(), createdAt = now, updatedAt = now)
        } else {
            note.copy(updatedAt = now)
        }
        repo.save(toSave)
        return toSave
    }
}
