package com.epic_engine.swisskit.feature.finance.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {

    @Query("SELECT * FROM finance_transactions ORDER BY date DESC")
    fun observeAll(): Flow<List<FinanceEntity>>

    @Query("SELECT * FROM finance_transactions ORDER BY date ASC")
    fun observeAllAscending(): Flow<List<FinanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FinanceEntity)

    @Update
    suspend fun update(entity: FinanceEntity)

    @Delete
    suspend fun delete(entity: FinanceEntity)

    @Query("DELETE FROM finance_transactions WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)

    @Query("SELECT * FROM finance_transactions WHERE id = :id")
    suspend fun getById(id: String): FinanceEntity?

    @Query("SELECT * FROM finance_transactions ORDER BY date DESC")
    suspend fun getAll(): List<FinanceEntity>

    @Query("DELETE FROM finance_transactions")
    suspend fun deleteAll()

    @Query("SELECT DISTINCT category FROM finance_transactions ORDER BY category ASC")
    fun observeDistinctCategories(): Flow<List<String>>
}
