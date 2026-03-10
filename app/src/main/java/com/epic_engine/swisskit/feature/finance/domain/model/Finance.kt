package com.epic_engine.swisskit.feature.finance.domain.model

data class Finance(
    val id: String,
    val title: String,
    val amount: Double,
    val date: Long,
    val notes: String?,
    val category: String,
    val type: FinanceType
)

enum class FinanceType {
    INCOME, EXPENSE
}

enum class FinanceSortOrder {
    DESCENDING, ASCENDING
}
