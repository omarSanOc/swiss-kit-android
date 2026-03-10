package com.epic_engine.swisskit.feature.notes.domain.usecase

import com.epic_engine.swisskit.feature.notes.domain.model.Note
import com.epic_engine.swisskit.feature.notes.domain.repository.NoteRepository
import javax.inject.Inject

class GetNoteByIdUseCase @Inject constructor(private val repo: NoteRepository) {
    suspend operator fun invoke(id: String): Note? = repo.getById(id)
}
