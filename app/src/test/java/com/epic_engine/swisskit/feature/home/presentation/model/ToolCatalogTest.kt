package com.epic_engine.swisskit.feature.home.presentation.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ToolCatalogTest {

    @Test
    fun catalog_contains_six_tools() {
        assertEquals(6, ToolCatalog.all.size)
    }

    @Test
    fun all_tool_ids_are_unique() {
        val ids = ToolCatalog.all.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun all_tools_have_non_blank_name_and_description() {
        ToolCatalog.all.forEach { tool ->
            assertTrue(tool.name.isNotEmpty())
            assertTrue(tool.description.isNotEmpty())
        }
    }

    @Test
    fun all_six_destinations_are_covered() {
        val destinations = ToolCatalog.all.map { it.destination }
        val expected = listOf(
            com.epic_engine.swisskit.navigation.SwissKitDestination.Shopping,
            com.epic_engine.swisskit.navigation.SwissKitDestination.Converter,
            com.epic_engine.swisskit.navigation.SwissKitDestination.Contacts,
            com.epic_engine.swisskit.navigation.SwissKitDestination.Finance,
            com.epic_engine.swisskit.navigation.SwissKitDestination.Notes,
            com.epic_engine.swisskit.navigation.SwissKitDestination.QrScanner
        )
        assertTrue(destinations.containsAll(expected))
    }

    @Test
    fun all_tools_have_distinct_destinations() {
        val destinations = ToolCatalog.all.map { it.destination }
        assertEquals(destinations.size, destinations.toSet().size)
    }
}
