package com.epic_engine.swisskit.feature.finance.domain.usecase

import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType
import javax.inject.Inject

class FilterFinanceUseCase @Inject constructor() {

    operator fun invoke(
        items: List<Finance>,
        query: String,
        selectedCategories: Set<String>,
        typeFilter: FinanceType?
    ): List<Finance> {
        return items.filter { item ->
            val matchesQuery = if (query.isBlank()) true else {
                val q = query.trim().lowercase()
                item.title.lowercase().contains(q) || (item.notes?.lowercase()?.contains(q) == true)
            }
            val matchesCategory = selectedCategories.isEmpty() || item.category in selectedCategories
            val matchesType = typeFilter == null || item.type == typeFilter
            matchesQuery && matchesCategory && matchesType
        }
    }
}
