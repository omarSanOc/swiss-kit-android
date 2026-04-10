package com.epic_engine.swisskit.feature.qrscanner

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.espresso.action.ViewActions.longClick
import com.epic_engine.swisskit.core.ui.theme.SwissKitTheme
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRContentType
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRScan
import com.epic_engine.swisskit.feature.qrscanner.presentation.components.QRScanItem
import org.junit.Rule
import org.junit.Test

class QRScanItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val urlScan = QRScan(
        id = "test-1",
        label = "Test label",
        content = "https://example.com",
        type = QRContentType.URL,
        scannedAt = 1_700_000_000_000L
    )

    private val textScan = QRScan(
        id = "test-2",
        label = "Plain text",
        content = "Texto sin acción",
        type = QRContentType.TEXT,
        scannedAt = 1_700_000_000_000L
    )

    // ── Render tests ──────────────────────────────────────────────────────────

    @Test
    fun rendersLabelInLightMode() {
        composeTestRule.setContent {
            SwissKitTheme(darkTheme = false, dynamicColor = false) {
                QRScanItem(
                    scan = urlScan,
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
                    scan = urlScan,
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
                    scan = urlScan,
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
                    scan = urlScan,
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

    // ── Swipe interaction tests ───────────────────────────────────────────────

    @Test
    fun swipeRevealsTresAccionesParaURL() {
        composeTestRule.setContent {
            SwissKitTheme(darkTheme = false, dynamicColor = false) {
                QRScanItem(
                    scan = urlScan,
                    onCopy = {},
                    onEditLabel = {},
                    onOpenContent = {},
                    onRequestDelete = {}
                )
            }
        }
        composeTestRule.onNodeWithTag("qr_scan_item").performTouchInput { swipeLeft() }
        composeTestRule.onNodeWithContentDescription("Abrir").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Editar etiqueta").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Eliminar").assertIsDisplayed()
    }

    @Test
    fun swipeRevealsDosAccionesParaTexto() {
        composeTestRule.setContent {
            SwissKitTheme(darkTheme = false, dynamicColor = false) {
                QRScanItem(
                    scan = textScan,
                    onCopy = {},
                    onEditLabel = {},
                    onOpenContent = {},
                    onRequestDelete = {}
                )
            }
        }
        composeTestRule.onNodeWithTag("qr_scan_item").performTouchInput { swipeLeft() }
        composeTestRule.onNodeWithContentDescription("Editar etiqueta").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Eliminar").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Abrir").assertDoesNotExist()
    }

    @Test
    fun tapEjecutaOnCopy() {
        var copied = false
        composeTestRule.setContent {
            SwissKitTheme(darkTheme = false, dynamicColor = false) {
                QRScanItem(
                    scan = urlScan,
                    onCopy = { copied = true },
                    onEditLabel = {},
                    onOpenContent = {},
                    onRequestDelete = {}
                )
            }
        }
        composeTestRule.onNodeWithText("https://example.com").performClick()
        assert(copied)
    }

    @Test
    fun longPressNoMuestraOpciones() {
        composeTestRule.setContent {
            SwissKitTheme(darkTheme = false, dynamicColor = false) {
                QRScanItem(
                    scan = urlScan,
                    onCopy = {},
                    onEditLabel = {},
                    onOpenContent = {},
                    onRequestDelete = {}
                )
            }
        }
        composeTestRule.onNodeWithText("https://example.com")
            .performTouchInput { longClick() }
        composeTestRule.onNodeWithText("Editar etiqueta").assertDoesNotExist()
        composeTestRule.onNodeWithText("Abrir").assertDoesNotExist()
    }

    @Test
    fun cadaBotonInvocaSuCallback() {
        var editCalled = false
        var deleteCalled = false
        var openCalled = false

        composeTestRule.setContent {
            SwissKitTheme(darkTheme = false, dynamicColor = false) {
                QRScanItem(
                    scan = urlScan,
                    onCopy = {},
                    onEditLabel = { editCalled = true },
                    onOpenContent = { openCalled = true },
                    onRequestDelete = { deleteCalled = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("qr_scan_item").performTouchInput { swipeLeft() }
        composeTestRule.onNodeWithContentDescription("Abrir").performClick()
        assert(openCalled)

        composeTestRule.onNodeWithTag("qr_scan_item").performTouchInput { swipeLeft() }
        composeTestRule.onNodeWithContentDescription("Editar etiqueta").performClick()
        assert(editCalled)

        composeTestRule.onNodeWithTag("qr_scan_item").performTouchInput { swipeLeft() }
        composeTestRule.onNodeWithContentDescription("Eliminar").performClick()
        assert(deleteCalled)
    }
}
