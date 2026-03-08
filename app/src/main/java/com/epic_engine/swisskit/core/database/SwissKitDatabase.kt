package com.epic_engine.swisskit.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.epic_engine.swisskit.feature.shopping.data.local.ShoppingDao
import com.epic_engine.swisskit.feature.shopping.data.local.ShoppingItemEntity

@Database(
    entities = [
        ShoppingItemEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class SwissKitDatabase : RoomDatabase() {
    abstract fun shoppingDao(): ShoppingDao
}
