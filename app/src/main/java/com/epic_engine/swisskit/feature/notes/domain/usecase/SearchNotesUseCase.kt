package com.epic_engine.swisskit.feature.notes.domain.usecase

import com.epic_engine.swisskit.feature.notes.domain.model.Note
import com.epic_engine.swisskit.feature.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import java.text.Normalizer
import javax.inject.Inject

class SearchNotesUseCase @Inject constructor(private val repo: NoteRepository) {
    operator fun invoke(rawQuery: String): Flow<List<Note>> {
        val normalized = normalize(rawQuery)
        return if (normalized.isBlank()) repo.observeAll()
        else repo.search(normalized)
    }

    companion object {
        fun normalize(text: String): String {
            val nfd = Normalizer.normalize(text, Normalizer.Form.NFD)
            return nfd.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
                .lowercase()
        }
    }
}
