package com.epic_engine.swisskit.feature.notes.presentation

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.designsystem.components.notesBackgroundBrush
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
    var showOverflowMenu by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current

    // Local TextFieldValue keeps cursor position for markdown formatting
    var contentFieldValue by remember { mutableStateOf(TextFieldValue(uiState.contentDraft)) }
    LaunchedEffect(uiState.contentDraft) {
        if (contentFieldValue.text != uiState.contentDraft) {
            contentFieldValue = contentFieldValue.copy(text = uiState.contentDraft)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                NoteDetailEvent.Saved, NoteDetailEvent.Deleted -> onNavigateBack()
                is NoteDetailEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is NoteDetailEvent.ReminderSet ->
                    snackbarHostState.showSnackbar("Recordatorio configurado")
            }
        }
    }

    // ── Markdown formatting helpers ───────────────────────────────────────────
    fun applyBold() {
        val sel = contentFieldValue.selection
        val text = contentFieldValue.text
        val selected = text.substring(sel.min, sel.max)
        val newText = text.substring(0, sel.min) + "**" + selected + "**" + text.substring(sel.max)
        val newCursor = if (sel.collapsed) sel.min + 2 else sel.max + 4
        contentFieldValue = TextFieldValue(newText, TextRange(newCursor))
        viewModel.onContentChange(newText)
    }

    fun applyItalic() {
        val sel = contentFieldValue.selection
        val text = contentFieldValue.text
        val selected = text.substring(sel.min, sel.max)
        val newText = text.substring(0, sel.min) + "*" + selected + "*" + text.substring(sel.max)
        val newCursor = if (sel.collapsed) sel.min + 1 else sel.max + 2
        contentFieldValue = TextFieldValue(newText, TextRange(newCursor))
        viewModel.onContentChange(newText)
    }

    fun applyBullet() {
        val sel = contentFieldValue.selection
        val text = contentFieldValue.text
        val lineStart = text.lastIndexOf('\n', sel.min - 1) + 1
        val newText = text.substring(0, lineStart) + "- " + text.substring(lineStart)
        val newCursor = sel.min + 2
        contentFieldValue = TextFieldValue(newText, TextRange(newCursor))
        viewModel.onContentChange(newText)
    }

    // ── Layout ────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(notesBackgroundBrush())
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text(
                        text = if (noteId == null) {"Crear Nota"} else {"Editar Nota"},
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                        )},
                    actions = {
                        Box {
                            IconButton(onClick = { showOverflowMenu = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.icon_ellipsis),
                                    contentDescription = "Más opciones",
                                    tint = if (isDark) Color.White else Color.Black
                                )
                            }
                            DropdownMenu(
                                expanded = showOverflowMenu,
                                onDismissRequest = { showOverflowMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Guardar nota") },
                                    onClick = {
                                        showOverflowMenu = false
                                        viewModel.onSave()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Guardar como recordatorio") },
                                    onClick = {
                                        showOverflowMenu = false
                                        viewModel.onSaveAndShowReminder()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Compartir nota") },
                                    onClick = {
                                        showOverflowMenu = false
                                        val shareText = buildString {
                                            if (uiState.titleDraft.isNotBlank()) {
                                                appendLine(uiState.titleDraft)
                                                appendLine()
                                            }
                                            append(uiState.contentDraft.toWhatsAppFormat())
                                        }
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_SUBJECT, uiState.titleDraft)
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Compartir nota"))
                                    }
                                )
                                if (uiState.note != null) {
                                    DropdownMenuItem(
                                        text = { Text("Eliminar", color = Color.Red) },
                                        onClick = {
                                            showOverflowMenu = false
                                            showDeleteDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                NoteFormattingToolbar(
                    onBold = ::applyBold,
                    onItalic = ::applyItalic,
                    onBullet = ::applyBullet
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title field (glassmorphism — thinMaterial 60 % alpha)
                BasicTextField(
                    value = uiState.titleDraft,
                    onValueChange = viewModel::onTitleChange,
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(NotesColors.Purple),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.60f))
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (uiState.titleDraft.isEmpty()) {
                                Text(
                                    text = "Titulo de la nota",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // Content field
                BasicTextField(
                    value = contentFieldValue,
                    onValueChange = { newValue ->
                        contentFieldValue = newValue
                        viewModel.onContentChange(newValue.text)
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(NotesColors.Purple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.60f))
                        .padding(12.dp),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.TopStart) {
                            if (contentFieldValue.text.isEmpty()) {
                                Text(
                                    text = "Escribe tu nota...",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
    }

    // ── Delete confirmation dialog ─────────────────────────────────────────────
    if (showDeleteDialog) {
        val noteName = uiState.note?.title?.ifBlank { "esta nota" } ?: "esta nota"
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar nota?") },
            text = { Text("Esta accion eliminara $noteName. No se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.onDelete()
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // ── Reminder bottom sheet ─────────────────────────────────────────────────
    if (uiState.showReminderPicker) {
        ReminderBottomSheet(
            isDark = isDark,
            onConfirm = viewModel::onSetReminder,
            onDismiss = viewModel::onDismissReminderPicker
        )
    }
}

// ── Formatting toolbar ────────────────────────────────────────────────────────

@Composable
private fun NoteFormattingToolbar(
    onBold: () -> Unit,
    onItalic: () -> Unit,
    onBullet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FormatTextButton(
                label = "B",
                fontWeight = FontWeight.Bold,
                contentDescription = "Bold",
                onClick = onBold
            )
            FormatTextButton(
                label = "I",
                fontStyle = FontStyle.Italic,
                contentDescription = "Italic",
                onClick = onItalic
            )
            FormatTextButton(
                label = "•",
                contentDescription = "Bullet list",
                onClick = onBullet
            )
        }
    }
}

@Composable
private fun FormatTextButton(
    label: String,
    contentDescription: String,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.defaultMinSize(minWidth = 32.dp, minHeight = 32.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = fontWeight,
                fontStyle = fontStyle
            )
        )
    }
}

// ── Reminder bottom sheet ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderBottomSheet(
    isDark: Boolean,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val accentColor = if (isDark) NotesColors.PurpleAccentDark else NotesColors.Purple
    val sheetBg = if (isDark) NotesColors.ReminderSheetDark else NotesColors.ReminderSheetLight

    // Internal navigation: date → time
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    if (!showTimePicker) {
        // ── Date picker ───────────────────────────────────────────────────────
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
        // ── Time picker in a ModalBottomSheet ─────────────────────────────────
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = sheetBg,
            shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Drag handle
                Surface(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .width(68.dp)
                        .height(6.dp),
                    shape = RoundedCornerShape(3.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                ) {}

                Text(
                    text = "Nuevo recordatorio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )

                TimePicker(state = timePickerState)

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                            onConfirm(cal.timeInMillis)
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

/**
 * Converts internal Markdown syntax to WhatsApp-compatible format.
 *
 * Internal → WhatsApp:
 *  **bold**  →  *bold*
 *  *italic*  →  _italic_
 *  - bullet  →  • bullet
 *
 * Uses a placeholder to avoid converting bold markers twice when
 * processing italic in the second pass.
 */
private fun String.toWhatsAppFormat(): String {
    val boldPlaceholder = "\u0001"
    // Pass 1 – protect bold spans (** … **) by replacing with a placeholder
    var result = Regex("""\*\*(.+?)\*\*""", RegexOption.DOT_MATCHES_ALL)
        .replace(this) { "$boldPlaceholder${it.groupValues[1]}$boldPlaceholder" }
    // Pass 2 – convert remaining single-asterisk italic to _underscore_
    result = Regex("""\*(.+?)\*""", RegexOption.DOT_MATCHES_ALL)
        .replace(result) { "_${it.groupValues[1]}_" }
    // Pass 3 – restore bold placeholders with WhatsApp bold (single asterisk)
    result = Regex("""\u0001(.+?)\u0001""", RegexOption.DOT_MATCHES_ALL)
        .replace(result) { "*${it.groupValues[1]}*" }
    // Pass 4 – convert Markdown bullets ("- text") to Unicode bullet ("• text")
    result = result.lines().joinToString("\n") { line ->
        if (line.startsWith("- ")) "• ${line.removePrefix("- ")}" else line
    }
    return result
}

private fun formatDateTime(epochMillis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(epochMillis))
}
