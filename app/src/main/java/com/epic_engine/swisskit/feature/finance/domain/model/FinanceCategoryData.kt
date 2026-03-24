package com.epic_engine.swisskit.feature.finance.domain.model

object FinanceCategoryData {

    val incomeCategories: List<String> = listOf("General")

    val expenseCategories: List<String> = listOf("General")

    fun categoriesFor(type: FinanceType): List<String> = when (type) {
        FinanceType.INCOME -> incomeCategories
        FinanceType.EXPENSE -> expenseCategories
    }
}
