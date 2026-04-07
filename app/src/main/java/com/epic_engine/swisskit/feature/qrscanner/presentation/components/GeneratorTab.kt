package com.epic_engine.swisskit.feature.qrscanner.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.DesignTokens
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
            .padding(DesignTokens.dimensMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DesignTokens.dimensMedium)
    ) {
        QRGeneratorInput(
            value = uiState.generatorInput,
            onValueChange = onInputChange,
            onGo = handleGenerate
        )

        SwissKitButton(
            text = stringResource(R.string.qr_generate_qr_title),
            onClick = handleGenerate,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.generatorInput.isNotBlank() && !uiState.isGenerating,
            containerColor = QRScannerDesignTokens.Primary
        )

        if (uiState.isGenerating) {
            CircularProgressIndicator(modifier = Modifier.size(DesignTokens.dimensXMedium), strokeWidth = DesignTokens.dimensXXXXSmall)
            Spacer(Modifier.width(DesignTokens.dimensXSmall))
        }

        uiState.generatedBitmap?.let { bitmap ->
            Card(
                shape = RoundedCornerShape(DesignTokens.dimensSmall),
                elevation = CardDefaults.cardElevation(defaultElevation = DesignTokens.dimensXXXSmall)
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = stringResource(R.string.qr_code_detected_title),
                    modifier = Modifier
                        .size(QRScannerDesignTokens.dimensXXXLarge)
                        .padding(DesignTokens.dimensMedium)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(DesignTokens.dimensXSmall)
            ) {
                SwissKitButton(
                    text = stringResource(R.string.qr_share_qr_title),
                    onClick = onShare,
                    modifier = Modifier.fillMaxWidth().padding(vertical = DesignTokens.dimensXXXSmall),
                    containerColor = QRScannerDesignTokens.Primary
                )

                SwissKitButton(
                    text = stringResource(R.string.qr_save_image_title),
                    onClick = onSaveToGallery,
                    modifier = Modifier.fillMaxWidth().padding(vertical = DesignTokens.dimensXXXXSmall),
                    containerColor = QRScannerDesignTokens.Primary
                )
            }
        }
    }
}