package com.epic_engine.swisskit.feature.notes.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.feature.notes.presentation.util.NoteMarkdownRenderer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: String?,
    onNavigateBack: () -> Unit,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                NoteDetailEvent.Saved, NoteDetailEvent.Deleted -> onNavigateBack()
                is NoteDetailEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is NoteDetailEvent.ReminderSet -> snackbarHostState.showSnackbar("Recordatorio configurado")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Editar nota" else "Nota") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (!uiState.isEditing && uiState.note != null) {
                        IconButton(onClick = viewModel::onTogglePreview) {
                            Icon(
                                imageVector = if (uiState.showMarkdownPreview) Icons.Default.Edit
                                else Icons.Default.Visibility,
                                contentDescription = if (uiState.showMarkdownPreview) "Ver raw" else "Vista previa"
                            )
                        }
                        IconButton(onClick = viewModel::onShowReminderPicker) {
                            Icon(Icons.Default.Alarm, contentDescription = "Recordatorio")
                        }
                        IconButton(onClick = viewModel::onToggleEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                    if (uiState.isEditing) {
                        IconButton(onClick = viewModel::onSave, enabled = !uiState.isSaving) {
                            Icon(Icons.Default.Save, contentDescription = "Guardar")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (uiState.isEditing) {
                OutlinedTextField(
                    value = uiState.titleDraft,
                    onValueChange = viewModel::onTitleChange,
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.contentDraft,
                    onValueChange = viewModel::onContentChange,
                    label = { Text("Contenido (Markdown)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 300.dp),
                    maxLines = Int.MAX_VALUE
                )
            } else {
                if (uiState.note?.title?.isNotBlank() == true) {
                    Text(
                        text = uiState.note!!.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                }
                if (uiState.showMarkdownPreview) {
                    Surface(
                        color = NotesDesignTokens.PreviewBackground,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = NoteMarkdownRenderer.render(uiState.note?.content ?: ""),
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    Text(
                        text = uiState.note?.content ?: "",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                uiState.reminderAt?.let { at ->
                    Spacer(Modifier.height(16.dp))
                    AssistChip(
                        onClick = viewModel::onClearReminder,
                        label = { Text("Recordatorio: ${formatDateTime(at)}") },
                        leadingIcon = {
                            Icon(Icons.Default.Alarm, null, tint = NotesDesignTokens.Primary)
                        },
                        trailingIcon = { Icon(Icons.Default.Close, null) }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar nota") },
            text = { Text("¿Estás seguro de que deseas eliminar esta nota?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.onDelete()
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (uiState.showReminderPicker) {
        NoteReminderPickerDialog(
            onConfirm = viewModel::onSetReminder,
            onDismiss = viewModel::onDismissReminderPicker
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteReminderPickerDialog(
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    val datePickerState = rememberDatePickerState()

    if (!showTimePicker) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null) showTimePicker = true
                }) { Text("Siguiente") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    } else {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Seleccionar hora") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    val dateMillis = selectedDateMillis ?: return@TextButton
                    val cal = Calendar.getInstance().apply {
                        timeInMillis = dateMillis
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onConfirm(cal.timeInMillis)
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        )
    }
}

private fun formatDateTime(epochMillis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(epochMillis))
}
