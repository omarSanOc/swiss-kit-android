package com.epic_engine.swisskit.feature.home.presentation.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ToolCatalogTest {

    @Test
    fun catalog_contains_six_tools() {
        assertThat(ToolCatalog.all).hasSize(6)
    }

    @Test
    fun all_tool_ids_are_unique() {
        val ids = ToolCatalog.all.map { it.id }
        assertThat(ids).containsNoDuplicates()
    }

    @Test
    fun all_tools_have_non_blank_name_and_description() {
        ToolCatalog.all.forEach { tool ->
            assertThat(tool.name).isNotEmpty()
            assertThat(tool.description).isNotEmpty()
        }
    }

    @Test
    fun all_six_destinations_are_covered() {
        val destinations = ToolCatalog.all.map { it.destination }
        assertThat(destinations).containsAtLeastElementsIn(
            listOf(
                com.epic_engine.swisskit.navigation.SwissKitDestination.Shopping,
                com.epic_engine.swisskit.navigation.SwissKitDestination.Converter,
                com.epic_engine.swisskit.navigation.SwissKitDestination.Contacts,
                com.epic_engine.swisskit.navigation.SwissKitDestination.Finance,
                com.epic_engine.swisskit.navigation.SwissKitDestination.Notes,
                com.epic_engine.swisskit.navigation.SwissKitDestination.QrScanner
            )
        )
    }

    @Test
    fun all_tools_have_distinct_destinations() {
        val destinations = ToolCatalog.all.map { it.destination }
        assertThat(destinations).containsNoDuplicates()
    }
}
