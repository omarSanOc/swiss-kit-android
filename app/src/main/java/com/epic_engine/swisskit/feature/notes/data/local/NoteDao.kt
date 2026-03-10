package com.epic_engine.swisskit.feature.notes.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes
        WHERE lower(title) LIKE '%' || lower(:query) || '%'
           OR lower(content) LIKE '%' || lower(:query) || '%'
        ORDER BY updatedAt DESC
    """)
    fun search(query: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getById(id: String): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: NoteEntity)

    @Delete
    suspend fun delete(entity: NoteEntity)

    @Query("DELETE FROM notes WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)
}
