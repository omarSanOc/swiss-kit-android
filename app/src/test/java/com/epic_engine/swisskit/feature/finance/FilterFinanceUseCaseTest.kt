package com.epic_engine.swisskit.feature.finance

import com.epic_engine.swisskit.feature.finance.domain.model.Finance
import com.epic_engine.swisskit.feature.finance.domain.model.FinanceType
import com.epic_engine.swisskit.feature.finance.domain.usecase.FilterFinanceUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FilterFinanceUseCaseTest {

    private lateinit var useCase: FilterFinanceUseCase
    private lateinit var items: List<Finance>

    @Before
    fun setUp() {
        useCase = FilterFinanceUseCase()
        items = listOf(
            Finance("1", "Salario mensual", 5000.0, 0L, null, "Salario", FinanceType.INCOME),
            Finance("2", "Renta departamento", 1500.0, 0L, "Pago mensual", "Vivienda", FinanceType.EXPENSE),
            Finance("3", "Freelance diseño", 800.0, 0L, null, "Freelance", FinanceType.INCOME),
            Finance("4", "Supermercado", 300.0, 0L, null, "Comida", FinanceType.EXPENSE),
        )
    }

    @Test
    fun `empty filters returns all items`() {
        val result = useCase(items, "", emptySet())
        assertEquals(4, result.size)
    }

    @Test
    fun `Todas filter returns all items`() {
        val result = useCase(items, "", setOf("Todas"))
        assertEquals(4, result.size)
    }

    @Test
    fun `text query filters by title`() {
        val result = useCase(items, "salario", emptySet())
        assertEquals(1, result.size)
        assertEquals("Salario mensual", result.first().title)
    }

    @Test
    fun `text query filters by notes`() {
        val result = useCase(items, "mensual", emptySet())
        // "Salario mensual" en título y "Pago mensual" en notas
        assertEquals(2, result.size)
    }

    @Test
    fun `Ingreso filter returns only income items`() {
        val result = useCase(items, "", setOf("Ingreso"))
        assertTrue(result.all { it.type == FinanceType.INCOME })
        assertEquals(2, result.size)
    }

    @Test
    fun `Gasto filter returns only expense items`() {
        val result = useCase(items, "", setOf("Gasto"))
        assertTrue(result.all { it.type == FinanceType.EXPENSE })
        assertEquals(2, result.size)
    }

    @Test
    fun `category filter returns matching items`() {
        val result = useCase(items, "", setOf("Vivienda", "Comida"))
        assertEquals(2, result.size)
        assertTrue(result.all { it.category in setOf("Vivienda", "Comida") })
    }

    @Test
    fun `combined filters are applied simultaneously`() {
        val result = useCase(items, "free", setOf("Freelance"))
        assertEquals(1, result.size)
        assertEquals("Freelance diseño", result.first().title)
    }

    @Test
    fun `query is case insensitive`() {
        val result = useCase(items, "RENTA", emptySet())
        assertEquals(1, result.size)
    }
}
