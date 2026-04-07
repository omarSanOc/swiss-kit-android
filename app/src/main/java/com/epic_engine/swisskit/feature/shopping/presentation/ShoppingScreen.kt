package com.epic_engine.swisskit.feature.shopping.presentation

import android.content.Intent
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.epic_engine.swisskit.R
import com.epic_engine.swisskit.core.ui.UiText
import com.epic_engine.swisskit.core.designsystem.DesignTokens
import com.epic_engine.swisskit.core.designsystem.components.SwissKitBackground
import com.epic_engine.swisskit.core.designsystem.components.SwissKitEmptyView
import com.epic_engine.swisskit.feature.home.presentation.theme.HomeDesignTokens
import com.epic_engine.swisskit.feature.shopping.presentation.components.ShoppingActionButtons
import com.epic_engine.swisskit.feature.shopping.presentation.components.ShoppingAddItemBar
import com.epic_engine.swisskit.feature.shopping.presentation.components.ShoppingDuplicateToast
import com.epic_engine.swisskit.feature.shopping.presentation.components.ShoppingItemRow
import com.epic_engine.swisskit.feature.shopping.presentation.theme.ShoppingDesignTokens
import com.epic_engine.swisskit.feature.shopping.presentation.utils.ShoppingEvent
import com.epic_engine.swisskit.feature.shopping.presentation.utils.ShoppingShareFormatter
import com.epic_engine.swisskit.feature.shopping.presentation.viewmodel.ShoppingViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    viewModel: ShoppingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var revealedItemId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message.asString(context))
            viewModel.onEvent(ShoppingEvent.ClearMessage)
        }
    }

    LaunchedEffect(uiState.duplicateMessage) {
        if (uiState.duplicateMessage != null) {
            delay(2500)
            viewModel.onEvent(ShoppingEvent.ClearDuplicateMessage)
        }
    }

    SwissKitBackground(
        colors = listOf(ShoppingDesignTokens.Primary, ShoppingDesignTokens.background),
        darkColors = listOf(ShoppingDesignTokens.Primary, ShoppingDesignTokens.darkBackground),
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(R.string.shopping_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                navigationIconContentColor = Color.White,
                                actionIconContentColor = Color.White
                            ),
                            actions = {
                                IconButton(
                                    onClick = {
                                        val text = ShoppingShareFormatter.buildShareText(uiState, context)
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, text)
                                        }
                                        context.startActivity(Intent.createChooser(intent, context.getString(R.string.shopping_share_chooser)))
                                    },
                                    enabled = uiState.hasAnyItems
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = stringResource(R.string.shopping_share_cd)
                                    )
                                }
                            }
                        )
                    },
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(start = DesignTokens.dimensMedium, top = DesignTokens.dimensXSmall, end = DesignTokens.dimensMedium, bottom = DesignTokens.dimensXXXMedium),
                        verticalArrangement = Arrangement.spacedBy(DesignTokens.dimensXSmall)
                    ) {
                        item(key = "add_bar") {
                            ShoppingAddItemBar(
                                value = uiState.inputText,
                                onValueChange = { viewModel.onEvent(ShoppingEvent.InputChanged(it)) },
                                onAdd = { viewModel.onEvent(ShoppingEvent.AddItem(uiState.inputText)) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        item(key = "action_buttons") {
                            ShoppingActionButtons(
                                checkedCount = uiState.checkedItems.size,
                                onUncheckAll = { viewModel.onEvent(ShoppingEvent.UncheckAll) },
                                onDeleteChecked = { viewModel.onEvent(ShoppingEvent.ShowDeleteCheckedDialog) }
                            )
                        }

                        if (!uiState.hasAnyItems) {
                            item(key = "empty") {
                                SwissKitEmptyView(
                                    icon = R.drawable.icon_shopping,
                                    title = stringResource(R.string.shopping_empty_title),
                                    subtitle = stringResource(R.string.shopping_empty_subtitle),
                                    modifier = Modifier.fillParentMaxSize(),
                                    iconTint = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        } else {
                            items(uiState.pendingItems, key = { it.id }) { item ->
                                ShoppingItemRow(
                                    item = item,
                                    isRevealed = revealedItemId == item.id,
                                    onRevealChange = { revealed ->
                                        revealedItemId = if (revealed) item.id else null
                                    },
                                    onToggle = { viewModel.onEvent(ShoppingEvent.ToggleItem(item)) },
                                    onDelete = { viewModel.onEvent(ShoppingEvent.ShowDeleteItemDialog(item)) },
                                    onEdit = { viewModel.onEvent(ShoppingEvent.StartEdit(item)) },
                                    modifier = Modifier.animateItem(
                                        fadeInSpec = tween(250, easing = EaseInOut),
                                        fadeOutSpec = tween(250, easing = EaseInOut)
                                    )
                                )
                            }

                            items(uiState.checkedItems, key = { it.id }) { item ->
                                ShoppingItemRow(
                                    item = item,
                                    isRevealed = revealedItemId == item.id,
                                    onRevealChange = { revealed ->
                                        revealedItemId = if (revealed) item.id else null
                                    },
                                    onToggle = { viewModel.onEvent(ShoppingEvent.ToggleItem(item)) },
                                    onDelete = { viewModel.onEvent(ShoppingEvent.ShowDeleteItemDialog(item)) },
                                    onEdit = { viewModel.onEvent(ShoppingEvent.StartEdit(item)) },
                                    modifier = Modifier.animateItem(
                                        fadeInSpec = tween(250, easing = EaseInOut),
                                        fadeOutSpec = tween(250, easing = EaseInOut)
                                    )
                                )
                            }
                        }
                    }
                }

                // Toast de duplicados — posicionado en la parte inferior derecha
                ShoppingDuplicateToast(
                    message = uiState.duplicateMessage?.asString(context),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(DesignTokens.dimensMedium)
                )
            }
        }
    )

    // Diálogo de confirmación: borrar marcados
    if (uiState.showDeleteCheckedDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ShoppingEvent.DismissDeleteDialog) },
            title = { Text(stringResource(R.string.shopping_delete_checked_title)) },
            text = { Text(stringResource(R.string.shopping_delete_checked_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onEvent(ShoppingEvent.DeleteChecked)
                        viewModel.onEvent(ShoppingEvent.DismissDeleteDialog)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text(stringResource(R.string.common_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(ShoppingEvent.DismissDeleteDialog) }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }

    // Diálogo de confirmación: eliminar ítem individual
    if (uiState.itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ShoppingEvent.DismissDeleteItemDialog) },
            title = { Text(stringResource(R.string.shopping_delete_item_title)) },
            text = { Text(stringResource(R.string.shopping_delete_item_message, uiState.itemToDelete!!.name)) },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onEvent(ShoppingEvent.ConfirmDeleteItem) },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text(stringResource(R.string.common_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(ShoppingEvent.DismissDeleteItemDialog) }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }

    // Diálogo de edición de ítem
    if (uiState.editingItem != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ShoppingEvent.CancelEdit) },
            title = { Text(stringResource(R.string.shopping_edit_title)) },
            text = {
                TextField(
                    value = uiState.editText,
                    onValueChange = { viewModel.onEvent(ShoppingEvent.EditTextChanged(it)) },
                    singleLine = true,
                    shape = RoundedCornerShape(DesignTokens.dimensSmall),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color(0xFFEEEEEE),
                        unfocusedContainerColor = Color(0xFFEEEEEE),
                        disabledContainerColor = Color(0xFFEEEEEE),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = ShoppingDesignTokens.Primary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (uiState.editText.isNotBlank()) {
                                viewModel.onEvent(ShoppingEvent.ConfirmEdit)
                            }
                        }
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onEvent(ShoppingEvent.ConfirmEdit) },
                    enabled = uiState.editText.isNotBlank()
                ) {
                    Text(stringResource(R.string.common_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(ShoppingEvent.CancelEdit) }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }
}
