package com.epic_engine.swisskit.feature.notes.domain.usecase

import com.epic_engine.swisskit.feature.notes.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteNotesUseCase @Inject constructor(private val repo: NoteRepository) {
    suspend operator fun invoke(ids: List<String>) = repo.deleteByIds(ids)
}
