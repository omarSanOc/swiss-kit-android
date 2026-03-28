package com.epic_engine.swisskit.feature.finance.domain.usecase

class FinanceCategorySelectionEngine {

    companion object {
        const val LABEL_ALL = "Todas"
        const val LABEL_INCOME = "Ingreso"
        const val LABEL_EXPENSE = "Gasto"
        private val SPECIAL = setOf(LABEL_ALL, LABEL_INCOME, LABEL_EXPENSE)
    }

    fun isSelected(category: String, selectedCategories: Set<String>): Boolean {
        return when {
            category == LABEL_ALL -> selectedCategories.isEmpty() || LABEL_ALL in selectedCategories
            category in SPECIAL   -> category in selectedCategories && LABEL_ALL !in selectedCategories
            else                  -> category in selectedCategories && selectedCategories.none { it in SPECIAL }
        }
    }

    fun toggleSelection(category: String, selectedCategories: Set<String>): Set<String> {
        return when {
            category == LABEL_ALL -> setOf(LABEL_ALL)

            category == LABEL_INCOME || category == LABEL_EXPENSE -> {
                if (isSelected(category, selectedCategories)) setOf(LABEL_ALL)
                else setOf(category)
            }

            else -> {
                val base = selectedCategories.filter { it !in SPECIAL }.toMutableSet()
                if (category in base) {
                    base.remove(category)
                    if (base.isEmpty()) setOf(LABEL_ALL) else base
                } else {
                    base.add(category)
                    base
                }
            }
        }
    }
}
