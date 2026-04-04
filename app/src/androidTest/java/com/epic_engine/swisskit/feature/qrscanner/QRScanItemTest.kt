package com.epic_engine.swisskit.feature.qrscanner

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRContentType
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.presentation.components.QRScanItem
import com.epic_engine.swisskit.ui.theme.SwissKitTheme
import org.junit.Rule
import org.junit.Test

class QRScanItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testScan = QRScan(
        id = "test-1",
        label = "Test label",
        content = "https://example.com",
        type = QRContentType.URL,
        scannedAt = 1_700_000_000_000L
    )

    @Test
    fun rendersLabelInLightMode() {
        composeTestRule.setContent {
            SwissKitTheme(darkTheme = false, dynamicColor = false) {
                QRScanItem(
                    scan = testScan,
                    onCopy = {},
                    onEditLabel = {},
                    onOpenContent = {},
                    onRequestDelete = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Test label").assertIsDisplayed()
    }

    @Test
    fun rendersContentInLightMode() {
        composeTestRule.setContent {
            SwissKitTheme(darkTheme = false, dynamicColor = false) {
                QRScanItem(
                    scan = testScan,
                    onCopy = {},
                    onEditLabel = {},
                    onOpenContent = {},
                    onRequestDelete = {}
                )
            }
        }
        composeTestRule.onNodeWithText("https://example.com").assertIsDisplayed()
    }

    @Test
    fun rendersDateInLightMode() {
        composeTestRule.setContent {
            SwissKitTheme(darkTheme = false, dynamicColor = false) {
                QRScanItem(
                    scan = testScan,
                    onCopy = {},
                    onEditLabel = {},
                    onOpenContent = {},
                    onRequestDelete = {}
                )
            }
        }
        // The year "2023" is present in all timezones for this epoch value
        composeTestRule.onNodeWithText("2023", substring = true).assertIsDisplayed()
    }

    @Test
    fun rendersLabelInDarkMode() {
        composeTestRule.setContent {
            SwissKitTheme(darkTheme = true, dynamicColor = false) {
                QRScanItem(
                    scan = testScan,
                    onCopy = {},
                    onEditLabel = {},
                    onOpenContent = {},
                    onRequestDelete = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Test label").assertIsDisplayed()
        composeTestRule.onNodeWithText("https://example.com").assertIsDisplayed()
    }
}
