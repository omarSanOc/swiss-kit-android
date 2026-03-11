package com.epic_engine.swisskit.feature.contacts.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts WHERE categoryId = :categoryId ORDER BY name ASC")
    fun observeByCategory(categoryId: String): Flow<List<ContactEntity>>

    @Query("""
        SELECT * FROM contacts
        WHERE categoryId = :categoryId
          AND lower(name) LIKE '%' || lower(:query) || '%'
        ORDER BY name ASC
    """)
    fun searchInCategory(categoryId: String, query: String): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getById(id: String): ContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ContactEntity)

    @Update
    suspend fun update(entity: ContactEntity)

    @Delete
    suspend fun delete(entity: ContactEntity)

    @Query("DELETE FROM contacts WHERE categoryId = :categoryId")
    suspend fun deleteAllInCategory(categoryId: String)
}
