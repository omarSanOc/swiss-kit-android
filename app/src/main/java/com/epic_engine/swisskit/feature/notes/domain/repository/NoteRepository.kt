package com.epic_engine.swisskit.feature.notes.domain.repository

import com.epic_engine.swisskit.feature.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun observeAll(): Flow<List<Note>>
    fun search(query: String): Flow<List<Note>>
    suspend fun getById(id: String): Note?
    suspend fun save(note: Note)
    suspend fun delete(note: Note)
    suspend fun deleteByIds(ids: List<String>)
}
