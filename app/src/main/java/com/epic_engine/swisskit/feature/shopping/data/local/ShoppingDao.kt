package com.epic_engine.swisskit.feature.shopping.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {

    @Query("SELECT * FROM shopping_items ORDER BY isChecked ASC, sortOrder ASC")
    fun observeAll(): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_items WHERE isChecked = 0 ORDER BY sortOrder ASC")
    fun observePending(): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_items WHERE isChecked = 1 ORDER BY sortOrder ASC")
    fun observeChecked(): Flow<List<ShoppingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingItemEntity)

    @Update
    suspend fun update(item: ShoppingItemEntity)

    @Delete
    suspend fun delete(item: ShoppingItemEntity)

    @Query("UPDATE shopping_items SET isChecked = 0")
    suspend fun uncheckAll()

    @Query("DELETE FROM shopping_items WHERE isChecked = 1")
    suspend fun deleteChecked()

    @Query("SELECT COUNT(*) FROM shopping_items WHERE LOWER(TRIM(name)) = LOWER(TRIM(:name))")
    suspend fun countByName(name: String): Int

    @Query("SELECT MAX(sortOrder) FROM shopping_items")
    suspend fun getMaxSortOrder(): Int?
}
