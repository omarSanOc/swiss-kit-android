package com.epic_engine.swisskit.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.epic_engine.swisskit.feature.finance.data.local.FinanceDao
import com.epic_engine.swisskit.feature.finance.data.local.FinanceEntity
import com.epic_engine.swisskit.feature.notes.data.local.NoteDao
import com.epic_engine.swisskit.feature.notes.data.local.NoteEntity
import com.epic_engine.swisskit.feature.shopping.data.local.ShoppingDao
import com.epic_engine.swisskit.feature.shopping.data.local.ShoppingItemEntity

@Database(
    entities = [
        ShoppingItemEntity::class,
        FinanceEntity::class,
        NoteEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class SwissKitDatabase : RoomDatabase() {
    abstract fun shoppingDao(): ShoppingDao
    abstract fun financeDao(): FinanceDao
    abstract fun noteDao(): NoteDao
}
