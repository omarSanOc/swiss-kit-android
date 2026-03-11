package com.epic_engine.swisskit.feature.qrscanner.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import com.epic_engine.swisskit.feature.qrscanner.presentation.QRScannerDesignTokens
import com.epic_engine.swisskit.feature.qrscanner.presentation.QRScannerUiState

@Composable
fun GeneratorTab(
    uiState: QRScannerUiState,
    onInputChange: (String) -> Unit,
    onGenerate: () -> Unit,
    onShare: () -> Unit,
    onSaveToGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = uiState.generatorInput,
            onValueChange = onInputChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Texto o URL para generar QR") },
            placeholder = { Text("https://ejemplo.com") },
            leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null) },
            trailingIcon = {
                if (uiState.generatorInput.isNotBlank()) {
                    IconButton(onClick = { onInputChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                    }
                }
            },
            maxLines = 4,
            keyboardActions = KeyboardActions(onGo = { onGenerate() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go)
        )

        Button(
            onClick = onGenerate,
            enabled = uiState.generatorInput.isNotBlank() && !uiState.isGenerating,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = QRScannerDesignTokens.Primary)
        ) {
            if (uiState.isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
            }
            Text("Generar QR")
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onSaveToGallery,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.SaveAlt, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Guardar")
                }
                Button(
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = QRScannerDesignTokens.Primary)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Compartir")
                }
            }
        }
    }
}
