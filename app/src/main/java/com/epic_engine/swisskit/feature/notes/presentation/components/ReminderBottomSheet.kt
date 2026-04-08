package com.epic_engine.swisskit.feature.notes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.feature.notes.domain.model.NoteReminderRecurrence
import com.epic_engine.swisskit.feature.notes.domain.model.NoteReminderRequest
import com.epic_engine.swisskit.feature.notes.presentation.theme.NotesDesignTokens
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderBottomSheet(
    isDark: Boolean,
    onConfirm: (NoteReminderRequest) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val accentColor = if (isDark) NotesDesignTokens.accent else NotesDesignTokens.Primary
    val sheetBg = if (isDark) NotesDesignTokens.ReminderSheetDark else NotesDesignTokens.ReminderSheetLight

    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var isDaily by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    if (!showTimePicker) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDateMillis = datePickerState.selectedDateMillis
                        if (selectedDateMillis != null) showTimePicker = true
                    }
                ) { Text("Siguiente", color = accentColor) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancelar", color = accentColor) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    } else {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = sheetBg,
            shape = RoundedCornerShape(topStart = DesignTokens.dimensLarge, topEnd = DesignTokens.dimensLarge),
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DesignTokens.dimensXXMedium)
                    .padding(bottom = DesignTokens.dimensXXXMedium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(DesignTokens.dimensSmall)
            ) {
                // Drag handle
                Surface(
                    modifier = Modifier
                        .padding(top = DesignTokens.dimensXSmall)
                        .width(NotesDesignTokens.dimensXXLarge)
                        .height(DesignTokens.dimensXXSmall),
                    shape = RoundedCornerShape(NotesDesignTokens.dimensXXSmall),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                ) {}

                Text(
                    text = "Nuevo recordatorio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = DesignTokens.dimensXXXSmall)
                )

                TimePicker(state = timePickerState)

                // Daily recurrence toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DesignTokens.dimensXSmall),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.note_reminder_daily_label),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isDaily,
                        onCheckedChange = { isDaily = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = accentColor)
                    )
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignTokens.dimensSmall)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar", color = accentColor)
                    }
                    androidx.compose.material3.Button(
                        onClick = {
                            val dateMillis = selectedDateMillis ?: return@Button
                            val cal = Calendar.getInstance().apply {
                                timeInMillis = dateMillis
                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                set(Calendar.MINUTE, timePickerState.minute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            val recurrence = if (isDaily) NoteReminderRecurrence.DAILY else NoteReminderRecurrence.ONE_TIME
                            onConfirm(NoteReminderRequest(cal.timeInMillis, recurrence))
                        },
                        modifier = Modifier.weight(1f),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}
