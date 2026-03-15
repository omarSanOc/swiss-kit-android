package com.epic_engine.swisskit.feature.shopping.presentation

import android.content.Intent
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.epic_engine.swisskit.core.designsystem.components.SwissKitEmptyView
import com.epic_engine.swisskit.feature.shopping.presentation.components.ShoppingActionButtons
import com.epic_engine.swisskit.feature.shopping.presentation.components.ShoppingAddItemBar
import com.epic_engine.swisskit.feature.shopping.presentation.components.ShoppingDuplicateToast
import com.epic_engine.swisskit.feature.shopping.presentation.components.ShoppingItemRow
import com.epic_engine.swisskit.ui.theme.yellowBackground
import com.epic_engine.swisskit.ui.theme.yellowShopping
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    onBack: () -> Unit,
    viewModel: ShoppingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var revealedItemId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(ShoppingEvent.ClearMessage)
        }
    }

    LaunchedEffect(uiState.duplicateMessage) {
        if (uiState.duplicateMessage != null) {
            delay(2500)
            viewModel.onEvent(ShoppingEvent.ClearDuplicateMessage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(yellowShopping, yellowBackground)
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Lista de compras",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                val text = viewModel.buildShareText()
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, text)
                                }
                                context.startActivity(Intent.createChooser(intent, "Compartir lista"))
                            },
                            enabled = uiState.hasAnyItems
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Compartir lista"
                            )
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShoppingAddItemBar(
                    value = uiState.inputText,
                    onValueChange = { viewModel.onEvent(ShoppingEvent.InputChanged(it)) },
                    onAdd = { viewModel.onEvent(ShoppingEvent.AddItem(uiState.inputText)) },
                    modifier = Modifier.fillMaxWidth()
                )

                ShoppingActionButtons(
                    checkedCount = uiState.checkedItems.size,
                    onUncheckAll = { viewModel.onEvent(ShoppingEvent.UncheckAll) },
                    onDeleteChecked = { viewModel.onEvent(ShoppingEvent.ShowDeleteCheckedDialog) }
                )

                if (!uiState.hasAnyItems) {
                    SwissKitEmptyView(
                        icon = Icons.Outlined.ShoppingCart,
                        title = "Tu lista está vacía",
                        subtitle = "Agrega ítems para empezar",
                        modifier = Modifier.fillMaxSize(),
                        iconTint = Color.White.copy(alpha = 0.7f)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
                    ) {
                        items(
                            items = uiState.pendingItems,
                            key = { it.id }
                        ) { item ->
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

                        items(
                            items = uiState.checkedItems,
                            key = { it.id }
                        ) { item ->
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
        }

        // Toast de duplicados — posicionado en la parte inferior derecha
        ShoppingDuplicateToast(
            message = uiState.duplicateMessage,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }

    // Diálogo de confirmación: borrar marcados
    if (uiState.showDeleteCheckedDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ShoppingEvent.DismissDeleteDialog) },
            title = { Text("Borrar marcados") },
            text = { Text("¿Estás seguro de que deseas eliminar todos los ítems marcados?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onEvent(ShoppingEvent.DeleteChecked)
                        viewModel.onEvent(ShoppingEvent.DismissDeleteDialog)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(ShoppingEvent.DismissDeleteDialog) }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de confirmación: eliminar ítem individual
    if (uiState.itemToDelete != null) {
        Dialog(
            onDismissRequest = { viewModel.onEvent(ShoppingEvent.DismissDeleteItemDialog) }
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "¿Eliminar producto?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Esta acción eliminará ${uiState.itemToDelete!!.name}. No se puede deshacer.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.onEvent(ShoppingEvent.DismissDeleteItemDialog) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEEEEEE),
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "Cancelar",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { viewModel.onEvent(ShoppingEvent.ConfirmDeleteItem) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEEEEEE),
                                contentColor = Color.Red
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "Eliminar",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de edición de ítem
    if (uiState.editingItem != null) {
        Dialog(
            onDismissRequest = { viewModel.onEvent(ShoppingEvent.CancelEdit) }
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Editar producto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    TextField(
                        value = uiState.editText,
                        onValueChange = { viewModel.onEvent(ShoppingEvent.EditTextChanged(it)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFEEEEEE),
                            unfocusedContainerColor = Color(0xFFEEEEEE),
                            disabledContainerColor = Color(0xFFEEEEEE),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = yellowShopping
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.onEvent(ShoppingEvent.CancelEdit) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEEEEEE),
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "Cancelar",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { viewModel.onEvent(ShoppingEvent.ConfirmEdit) },
                            enabled = uiState.editText.isNotBlank(),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEEEEEE),
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "Guardar",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
