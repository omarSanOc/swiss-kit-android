package com.epic_engine.swisskit.core.designsystem

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.epic_engine.swisskit.core.designsystem.components.SwissKitTabPicker
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SwissKitTabPickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rendersAllLabels() {
        composeTestRule.setContent {
            SwissKitTabPicker(
                options = listOf("Unidades", "Divisas"),
                selectedIndex = 0,
                onTabSelected = {}
            )
        }

        composeTestRule.onNodeWithText("Unidades").assertIsDisplayed()
        composeTestRule.onNodeWithText("Divisas").assertIsDisplayed()
    }

    @Test
    fun callbackReceivesCorrectIndex() {
        var tappedIndex = -1

        composeTestRule.setContent {
            SwissKitTabPicker(
                options = listOf("Unidades", "Divisas"),
                selectedIndex = 0,
                onTabSelected = { tappedIndex = it }
            )
        }

        composeTestRule.onNodeWithText("Divisas").performClick()

        assertEquals(1, tappedIndex)
    }

    @Test
    fun selectedIndexChangesActiveTab() {
        var selected by mutableIntStateOf(0)

        composeTestRule.setContent {
            SwissKitTabPicker(
                options = listOf("Unidades", "Divisas"),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        }

        composeTestRule.onNodeWithText("Divisas").performClick()
        assertEquals(1, selected)

        composeTestRule.onNodeWithText("Unidades").performClick()
        assertEquals(0, selected)
    }

    @Test
    fun rendersWith3OrMoreTabs() {
        composeTestRule.setContent {
            SwissKitTabPicker(
                options = listOf("A", "B", "C"),
                selectedIndex = 0,
                onTabSelected = {}
            )
        }

        composeTestRule.onNodeWithText("A").assertIsDisplayed()
        composeTestRule.onNodeWithText("B").assertIsDisplayed()
        composeTestRule.onNodeWithText("C").assertIsDisplayed()
    }

    @Test
    fun emptyOptionsRendersNothing() {
        composeTestRule.setContent {
            SwissKitTabPicker(
                options = emptyList(),
                selectedIndex = 0,
                onTabSelected = {},
                modifier = Modifier
            )
        }

        // No node should exist — the composable returns early
        composeTestRule.onNodeWithText("").assertDoesNotExist()
    }
}
