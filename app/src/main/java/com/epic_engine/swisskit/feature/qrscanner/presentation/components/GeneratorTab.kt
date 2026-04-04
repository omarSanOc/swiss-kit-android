package com.epic_engine.swisskit.feature.qrscanner.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.components.SwissKitButton
import com.epic_engine.swisskit.feature.qrscanner.presentation.theme.QRScannerDesignTokens
import com.epic_engine.swisskit.feature.qrscanner.presentation.util.QRScannerUiState

@Composable
fun GeneratorTab(
    uiState: QRScannerUiState,
    onInputChange: (String) -> Unit,
    onGenerate: () -> Unit,
    onShare: () -> Unit,
    onSaveToGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val handleGenerate = {
        if (uiState.generatorInput.isNotBlank()) {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
            onGenerate()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QRGeneratorInput(
            value = uiState.generatorInput,
            onValueChange = onInputChange,
            onGo = handleGenerate
        )

        SwissKitButton(
            text = "Generar QR",
            onClick = handleGenerate,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.generatorInput.isNotBlank() && !uiState.isGenerating,
            containerColor = QRScannerDesignTokens.Primary
        )

        if (uiState.isGenerating) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
        }

        uiState.generatedBitmap?.let { bitmap ->
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Código QR generado",
                    modifier = Modifier
                        .size(280.dp)
                        .padding(16.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SwissKitButton(
                    text = "Compartir QR",
                    onClick = onShare,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    containerColor = QRScannerDesignTokens.Primary
                )

                SwissKitButton(
                    text = "Guardar imagen",
                    onClick = onSaveToGallery,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    containerColor = QRScannerDesignTokens.Primary
                )
            }
        }
    }
}