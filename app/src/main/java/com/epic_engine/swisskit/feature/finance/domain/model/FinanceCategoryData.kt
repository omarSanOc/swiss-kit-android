package com.epic_engine.swisskit.feature.finance.domain.model

object FinanceCategoryData {

    val incomeCategories: List<String> = listOf(
        "Salario", "Freelance", "Inversión", "Negocio", "Regalo", "Bono", "Dividendo", "Otro"
    )

    val expenseCategories: List<String> = listOf(
        "Comida", "Transporte", "Vivienda", "Entretenimiento", "Salud",
        "Educación", "Ropa", "Servicios", "Suscripción", "Deuda", "Otro"
    )

    fun categoriesFor(type: FinanceType): List<String> = when (type) {
        FinanceType.INCOME -> incomeCategories
        FinanceType.EXPENSE -> expenseCategories
    }
}
