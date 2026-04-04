package com.epic_engine.swisskit.feature.qrscanner.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.feature.qrscanner.domain.model.QRContentType
import com.epic_engine.swisskit.feature.qrscanner.presentation.theme.QRScannerDesignTokens
import com.epic_engine.swisskit.feature.qrscanner.presentation.util.PendingQRResult

private val openableTypes = setOf(
    QRContentType.URL,
    QRContentType.EMAIL,
    QRContentType.PHONE,
    QRContentType.LOCATION
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultBottomSheet(
    pendingResult: PendingQRResult,
    initialLabel: String,
    onSave: (content: String, label: String) -> Unit,
    onDismiss: () -> Unit,
    onOpenContent: (PendingQRResult) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var label by remember(initialLabel) { mutableStateOf(initialLabel) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Código detectado",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = pendingResult.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            if (pendingResult.type in openableTypes) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onOpenContent(pendingResult); onDismiss() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Abrir")
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Etiqueta") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.End)
            ) {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
                Button(
                    onClick = { onSave(pendingResult.content, label) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = QRScannerDesignTokens.Primary
                    )
                ) {
                    Text("Guardar")
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
