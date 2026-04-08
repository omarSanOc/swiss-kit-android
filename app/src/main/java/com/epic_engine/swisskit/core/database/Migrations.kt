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

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `categories` (
                `id` TEXT NOT NULL,
                `title` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `contacts` (
                `id` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `phone` TEXT NOT NULL,
                `categoryId` TEXT NOT NULL,
                PRIMARY KEY(`id`),
                FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`)
                    ON DELETE CASCADE
            )
        """)
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_contacts_categoryId` ON `contacts` (`categoryId`)"
        )
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE notes ADD COLUMN reminderRecurrence TEXT")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `qr_scans` (
                `id` TEXT NOT NULL,
                `content` TEXT NOT NULL,
                `type` TEXT NOT NULL,
                `label` TEXT NOT NULL,
                `scannedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)
    }
}
