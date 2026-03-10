package com.epic_engine.swisskit.feature.notes.data.repository

import com.epic_engine.swisskit.feature.notes.data.local.NoteDao
import com.epic_engine.swisskit.feature.notes.data.mapper.toDomain
import com.epic_engine.swisskit.feature.notes.data.mapper.toEntity
import com.epic_engine.swisskit.feature.notes.domain.model.Note
import com.epic_engine.swisskit.feature.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val dao: NoteDao
) : NoteRepository {

    override fun observeAll(): Flow<List<Note>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun search(query: String): Flow<List<Note>> =
        dao.search(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): Note? =
        dao.getById(id)?.toDomain()

    override suspend fun save(note: Note) =
        dao.upsert(note.toEntity())

    override suspend fun delete(note: Note) =
        dao.delete(note.toEntity())

    override suspend fun deleteByIds(ids: List<String>) =
        dao.deleteByIds(ids)
}
