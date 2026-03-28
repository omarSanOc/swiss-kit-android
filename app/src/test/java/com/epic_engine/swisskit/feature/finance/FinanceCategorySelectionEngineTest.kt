package com.epic_engine.swisskit.feature.finance

import com.epic_engine.swisskit.feature.finance.domain.usecase.FinanceCategorySelectionEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FinanceCategorySelectionEngineTest {

    private lateinit var engine: FinanceCategorySelectionEngine

    @Before
    fun setUp() {
        engine = FinanceCategorySelectionEngine()
    }

    // ── isSelected ──────────────────────────────────────────────────────────

    @Test
    fun `Todas is selected when set is empty`() {
        assertTrue(engine.isSelected("Todas", emptySet()))
    }

    @Test
    fun `Todas is selected when set contains Todas`() {
        assertTrue(engine.isSelected("Todas", setOf("Todas")))
    }

    @Test
    fun `Ingreso is selected when set contains only Ingreso`() {
        assertTrue(engine.isSelected("Ingreso", setOf("Ingreso")))
        assertFalse(engine.isSelected("Gasto", setOf("Ingreso")))
        assertFalse(engine.isSelected("Todas", setOf("Ingreso")))
    }

    @Test
    fun `dynamic category is selected when set has only that category`() {
        assertTrue(engine.isSelected("Salario", setOf("Salario")))
        assertFalse(engine.isSelected("Todas", setOf("Salario")))
        assertFalse(engine.isSelected("Ingreso", setOf("Salario")))
    }

    @Test
    fun `dynamic category is not selected when a special chip is present`() {
        assertFalse(engine.isSelected("Salario", setOf("Ingreso", "Salario")))
    }

    // ── toggleSelection ─────────────────────────────────────────────────────

    @Test
    fun `tapping Todas always resets to setOf Todas`() {
        assertEquals(setOf("Todas"), engine.toggleSelection("Todas", setOf("Ingreso")))
        assertEquals(setOf("Todas"), engine.toggleSelection("Todas", setOf("Salario")))
        assertEquals(setOf("Todas"), engine.toggleSelection("Todas", setOf("Todas")))
    }

    @Test
    fun `tapping Ingreso when Todas is active selects Ingreso`() {
        assertEquals(setOf("Ingreso"), engine.toggleSelection("Ingreso", setOf("Todas")))
    }

    @Test
    fun `tapping Ingreso again deselects and falls back to Todas`() {
        assertEquals(setOf("Todas"), engine.toggleSelection("Ingreso", setOf("Ingreso")))
    }

    @Test
    fun `tapping Gasto when Ingreso is active switches to Gasto (mutual exclusion)`() {
        assertEquals(setOf("Gasto"), engine.toggleSelection("Gasto", setOf("Ingreso")))
    }

    @Test
    fun `tapping a dynamic category removes special chips`() {
        assertEquals(setOf("Salario"), engine.toggleSelection("Salario", setOf("Todas")))
        assertEquals(setOf("Salario"), engine.toggleSelection("Salario", setOf("Ingreso")))
    }

    @Test
    fun `tapping an active dynamic category deselects and falls back to Todas`() {
        assertEquals(setOf("Todas"), engine.toggleSelection("Salario", setOf("Salario")))
    }

    @Test
    fun `tapping a new dynamic category adds to existing dynamic set`() {
        val result = engine.toggleSelection("Freelance", setOf("Salario"))
        assertEquals(setOf("Salario", "Freelance"), result)
    }

    @Test
    fun `deselecting one of two dynamic categories keeps the other`() {
        val result = engine.toggleSelection("Salario", setOf("Salario", "Freelance"))
        assertEquals(setOf("Freelance"), result)
    }
}
