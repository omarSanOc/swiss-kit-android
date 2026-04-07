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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.ui.UiText
import com.epic_engine.swisskit.core.designsystem.components.SwissKitBackground
import com.epic_engine.swisskit.core.designsystem.components.notesBackgroundBrush
import com.epic_engine.swisskit.feature.notes.presentation.components.NoteFormattingToolbar
import com.epic_engine.swisskit.feature.notes.presentation.components.ReminderBottomSheet
import com.epic_engine.swisskit.feature.notes.presentation.theme.NotesDesignTokens
import com.epic_engine.swisskit.feature.notes.presentation.utils.NoteDetailEvent
import com.epic_engine.swisskit.feature.notes.presentation.utils.toWhatsAppFormat
import com.epic_engine.swisskit.feature.notes.presentation.viewmodel.NoteDetailViewModel
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
                is NoteDetailEvent.ShowError -> snackbarHostState.showSnackbar(event.message.asString(context))
                is NoteDetailEvent.ReminderSet ->
                    snackbarHostState.showSnackbar(context.getString(R.string.note_detail_reminder_set))
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
    SwissKitBackground(
        colors = listOf(NotesDesignTokens.Primary, NotesDesignTokens.background),
        darkColors = listOf(NotesDesignTokens.Primary, NotesDesignTokens.darkBackground),
        content = {
            Scaffold(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = { Text(
                            text = stringResource(if (noteId == null) R.string.note_detail_create_title else R.string.note_detail_edit_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )},
                        actions = {
                            Box {
                                IconButton(onClick = { showOverflowMenu = true }) {
                                    Icon(
                                        painter = painterResource(R.drawable.icon_ellipsis),
                                        contentDescription = stringResource(R.string.note_detail_more_options_cd),
                                        tint = if (isDark) Color.White else Color.Black
                                    )
                                }
                                DropdownMenu(
                                    expanded = showOverflowMenu,
                                    onDismissRequest = { showOverflowMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.note_detail_save_menu)) },
                                        onClick = {
                                            showOverflowMenu = false
                                            viewModel.onSave()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.note_detail_save_reminder_menu)) },
                                        onClick = {
                                            showOverflowMenu = false
                                            viewModel.onSaveAndShowReminder()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.note_detail_share_menu)) },
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
                                            context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.note_detail_share_chooser)))
                                        }
                                    )
                                    if (uiState.note != null) {
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.common_delete), color = Color.Red) },
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
                        cursorBrush = SolidColor(NotesDesignTokens.Primary),
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
                                        text = stringResource(R.string.note_detail_title_placeholder),
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

                    NoteFormattingToolbar(
                        onBold = ::applyBold,
                        onItalic = ::applyItalic,
                        onBullet = ::applyBullet
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
                        cursorBrush = SolidColor(NotesDesignTokens.Primary),
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
                                        text = stringResource(R.string.note_detail_content_placeholder),
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
    )



    // ── Delete confirmation dialog ─────────────────────────────────────────────
    if (showDeleteDialog) {
        val noteName = uiState.note?.title?.ifBlank { "esta nota" } ?: "esta nota"
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.note_detail_delete_title)) },
            text = { Text(stringResource(R.string.note_detail_delete_message, noteName)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.onDelete()
                }) {
                    Text(stringResource(R.string.common_delete), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.common_cancel)) }
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