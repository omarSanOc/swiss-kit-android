package com.epic_engine.swisskit.feature.finance.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "finance_transactions")
data class FinanceEntity(
    @PrimaryKey val id: String,
    val title: String,
    val amount: Double,
    val date: Long,
    val notes: String?,
    val category: String,
    val type: String
)
