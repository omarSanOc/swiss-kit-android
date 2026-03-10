package com.epic_engine.swisskit.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `finance_transactions` (
                `id` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                `amount` REAL NOT NULL,
                `date` INTEGER NOT NULL,
                `notes` TEXT,
                `category` TEXT NOT NULL,
                `type` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `notes` (
                `id` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                `content` TEXT NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                `reminderAt` INTEGER,
                PRIMARY KEY(`id`)
            )
        """)
    }
}
