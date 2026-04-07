package com.epic_engine.swisskit.feature.finance.domain.usecase

class FinanceCategorySelectionEngine {

    companion object {
        const val KEY_ALL = "__finance_filter_all__"
        const val KEY_INCOME = "__finance_filter_income__"
        const val KEY_EXPENSE = "__finance_filter_expense__"

        private val SPECIAL = setOf(KEY_ALL, KEY_INCOME, KEY_EXPENSE)
    }

    fun isSelected(category: String, selectedCategories: Set<String>): Boolean {
        return when {
            category == KEY_ALL -> selectedCategories.isEmpty() || KEY_ALL in selectedCategories
            category in SPECIAL -> category in selectedCategories && KEY_ALL !in selectedCategories
            else                  -> category in selectedCategories && selectedCategories.none { it in SPECIAL }
        }
    }

    fun toggleSelection(category: String, selectedCategories: Set<String>): Set<String> {
        return when {
            category == KEY_ALL -> setOf(KEY_ALL)

            category == KEY_INCOME || category == KEY_EXPENSE -> {
                if (isSelected(category, selectedCategories)) setOf(KEY_ALL)
                else setOf(category)
            }

            else -> {
                val base = selectedCategories.filter { it !in SPECIAL }.toMutableSet()
                if (category in base) {
                    base.remove(category)
                    if (base.isEmpty()) setOf(KEY_ALL) else base
                } else {
                    base.add(category)
                    base
                }
            }
        }
    }
}
