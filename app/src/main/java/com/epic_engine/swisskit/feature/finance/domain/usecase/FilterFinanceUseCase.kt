package com.epic_engine.swisskit.feature.finance.domain.usecase

import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType
import javax.inject.Inject

class FilterFinanceUseCase @Inject constructor() {

    operator fun invoke(
        items: List<Finance>,
        query: String,
        selectedCategories: Set<String>
    ): List<Finance> {
        return items.filter { item ->
            val matchesQuery = if (query.isBlank()) true else {
                val q = query.trim().lowercase()
                item.title.lowercase().contains(q) || (item.notes?.lowercase()?.contains(q) == true)
            }
            val matchesCategory = when {
                selectedCategories.isEmpty() || FinanceCategorySelectionEngine.LABEL_ALL in selectedCategories -> true
                FinanceCategorySelectionEngine.LABEL_INCOME in selectedCategories -> item.type == FinanceType.INCOME
                FinanceCategorySelectionEngine.LABEL_EXPENSE in selectedCategories -> item.type == FinanceType.EXPENSE
                else -> item.category in selectedCategories
            }
            matchesQuery && matchesCategory
        }
    }
}
