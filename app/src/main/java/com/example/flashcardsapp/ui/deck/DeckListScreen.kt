package com.example.flashcardsapp.ui.deck

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcardsapp.R
import com.example.flashcardsapp.data.entity.Deck
import com.example.flashcardsapp.ui.FlashCardAppViewModelProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckListScreen(
    navigateToDeck: (Int) -> Unit,
    viewModel: DeckListViewModel = viewModel(factory = FlashCardAppViewModelProvider.Factory)
) {
    val deckListUiState by viewModel.deckListUiState.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val inSelectionMode by viewModel.inSelectionMode.collectAsState()

    val showCreateDialog = viewModel.showCreateDialog
    val showUpdateDialog = viewModel.showUpdateDialog
    val showDeleteConfirm = viewModel.showDeleteConfirm
    val deckToUpdate = viewModel.deckToUpdate

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateDialog() },
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.default_padding))
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_deck_btn_desc)
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    AnimatedVisibility(
                        visible = inSelectionMode,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row {
                            IconButton(
                                onClick = { viewModel.exitSelectionMode() }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Exit selection mode icon",
                                )
                            }
                        }
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = selectedIds.size == 1
                    ) {
                        IconButton(
                            onClick = { viewModel.openUpdateDialog() }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.rename),
                                contentDescription = "Rename Icon",
                                modifier = Modifier.size(dimensionResource(R.dimen.icon_top_bar_size))
                            )
                        }
                    }

                    AnimatedVisibility (
                        visible = selectedIds.isNotEmpty()
                    ) {
                        IconButton(
                            onClick = { viewModel.openDeleteConfirm() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Icon",
                                modifier = Modifier.size(dimensionResource(R.dimen.icon_top_bar_size))
                            )
                        }
                    }
                },
                modifier = Modifier.shadow(elevation = dimensionResource(R.dimen.top_app_bar_elevation))
            )
        },
        modifier = Modifier.shadow(elevation = dimensionResource(R.dimen.top_app_bar_elevation))
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            AnimatedVisibility(
                visible = inSelectionMode
            ) {
                Row {
                    Column {
                        if (selectedIds.size != deckListUiState.decks.size) {
                            IconButton(
                                onClick = { viewModel.selectAll(deckListUiState.decks) },
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.radio_btn_unchecked),
                                    contentDescription = "Select All",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                )
                            }
                        }

                        if (selectedIds.size == deckListUiState.decks.size) {
                            IconButton(
                                onClick = { viewModel.deselectAll() },
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Deselect All",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }

                    Text(
                        text = "${selectedIds.size} selected",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
            }

            DeckList(
                decks = deckListUiState.decks,
                contentPadding = PaddingValues(0.dp),
                selectedIds = selectedIds,
                inSelectionMode = inSelectionMode,
                onNavigateToDeck = navigateToDeck,
                onDeckClicked = { id, selected ->
                    viewModel.toggleDeckSelected(id, selected)
                },
                onMultipleSelected = { k1, k2, k3 -> viewModel.addRangeToSelection(k1, k2, k3) },
                addDeckToSelection = { viewModel.addDeckToSelection(it) }
            ) {
                viewModel.enterSelectionMode()
            }
        }

        AnimatedVisibility(
            visible = showDeleteConfirm
        ) {
            BasicAlertDialog(
                onDismissRequest = { viewModel.closeDeleteConfirm() },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    usePlatformDefaultWidth = true
                ),
                modifier = Modifier.padding(dimensionResource(R.dimen.default_padding))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.default_padding))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Delete warning",
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            text = "Are you sure you want to delete " +
                                    " ${if (selectedIds.size > 1) "these" else "this"} " +
                                    " deck${if (selectedIds.size > 1) "s" else ""}?",
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = { viewModel.closeDeleteConfirm() },
                            ) {
                                Text(
                                    text = stringResource(R.string.cancel),
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                                )
                            }

                            TextButton(
                                onClick = { viewModel.deleteByIds(selectedIds) }
                            ) {
                                Text(
                                    text = stringResource(R.string.confirm),
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showCreateDialog,
            exit = ExitTransition.None
        ) {
            CreateDeckDialog(
                uiState = viewModel.createDeckUiState,
                onCancel = { viewModel.closeCreateDialog() },
                onCreate = { viewModel.createDeck() },
                onTextValueChange = { viewModel.updateCreateUiState(it) },
                modifier = Modifier.padding(dimensionResource(R.dimen.default_padding))
            )
        }

        AnimatedVisibility(
            visible = showUpdateDialog && deckToUpdate != null,
            exit = ExitTransition.None
        ) {
            CreateDeckDialog(
                uiState = viewModel.createDeckUiState,
                onCancel = { viewModel.closeUpdateDialog() },
                onCreate = { viewModel.updateDeck() },
                onTextValueChange = { viewModel.updateCreateUiState(it) },
                modifier = Modifier.padding(dimensionResource(R.dimen.default_padding))
            )
        }
    }
}

@Composable
private fun DeckList(
    decks: List<Deck>,
    selectedIds: Set<Int>,
    onDeckClicked: (Int, Boolean) -> Unit,
    addDeckToSelection: (Int) -> Unit,
    inSelectionMode: Boolean,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onNavigateToDeck: (Int) -> Unit,
    onMultipleSelected: (Int, Int, Int) -> Unit,
    enterSelectionMode: () -> Unit,
) {
    val state = rememberLazyGridState()

    // defines scroll speed when in selection mode and dragging
    val autoScrollSpeed = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(autoScrollSpeed.floatValue) {
        if (autoScrollSpeed.floatValue != 0f) {
            while (isActive) {
                state.scrollBy(autoScrollSpeed.floatValue)
                delay(10)
            }
        }
    }
//    val state = rememberLazyGridState()

    if (decks.isEmpty()) {
        Text(
            text = stringResource(R.string.no_decks_available),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        )
        return
    }

    LazyVerticalGrid(
        state = state,
        columns = GridCells.Adaptive(75.dp),
        modifier = Modifier
            .padding(contentPadding)
            .selectDragHandler(
                lazyGridState = state,
                haptics = LocalHapticFeedback.current,
                autoScrollSpeed = autoScrollSpeed,
                autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() },
                onMultipleSelected = onMultipleSelected,
                onOneSelected = addDeckToSelection,
                selectedIds = selectedIds
            )
    ) {
        itemsIndexed(
            items = decks,
            key = { index, _ -> index }
        ) { index, deck ->
            val selected = selectedIds.contains(index)

            DeckItem(
                deck = deck,
                inSelectionMode = inSelectionMode,
                selected = selected,
                modifier = Modifier
                    .pointerInput(Unit) {
                        // on long press, enter selection mode
                        detectTapGestures(
                            onLongPress = {
                                enterSelectionMode()
                                addDeckToSelection(index)
                            },
                            onTap = {
                                if (!inSelectionMode) {
                                    onNavigateToDeck(deck.id)
                                }
                            }
                        )
                    }
                    .then(
                        if (inSelectionMode) {
                            // when in select mode, if the item is tapped,
                            // make it toggleable
                            Modifier.toggleable(
                                value = selected,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onValueChange = { onDeckClicked(index, it) }
                            )
                        } else Modifier
                    )
            )
        }
    }
}

@Composable
private fun DeckItem(
    deck: Deck,
    inSelectionMode: Boolean,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(selected, label = "selected")

    val bgColor by transition.animateColor {
        if (it) Color.Black.copy(alpha = 0.05f) else Color.Unspecified
    }

    Box(
        modifier = modifier
            .background(bgColor)
    ) {
        // normal deck item
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.deck_item_padding))
        ) {
            Image(
                painter = painterResource(R.drawable.flash_card),
                contentDescription = stringResource(R.string.deck_icon_desc),
                modifier = Modifier
                    .size(dimensionResource(R.dimen.deck_icon_size))
            )

            Text(
                text = deck.name,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // selection
        if (!inSelectionMode) {
            return
        }

        if (selected) {
            val selectedBgColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Selected deck",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.deck_item_padding))
                    .border(2.dp, selectedBgColor, CircleShape)
                    .clip(CircleShape)
                    .background(selectedBgColor)
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.radio_btn_unchecked),
                contentDescription = "Not Selected Deck",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.deck_item_padding))
            )
        }
    }
}

@Composable
private fun CreateDeckDialog(
    uiState: CreateDeckUiState,
    onCancel: () -> Unit,
    onCreate: () -> Unit,
    onTextValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = {}
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(dimensionResource(R.dimen.default_padding))
        ) {
            val focusRequester = remember { FocusRequester() }
            OutlinedTextField(
                value = uiState.deckName,
                onValueChange = onTextValueChange,
                label = { Text(stringResource(R.string.deck_name_hint)) },
                modifier = modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            if (!uiState.errorMessage.isNullOrBlank()) {
                Text(
                    text = uiState.errorMessage,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = modifier
                    .fillMaxWidth()
            ) {
                TextButton(
                    onClick = onCancel
                ) {
                    Text(stringResource(R.string.cancel))
                }

                TextButton(
                    onClick = onCreate
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

fun Modifier.selectDragHandler(
    lazyGridState: LazyGridState,
    haptics: HapticFeedback,
    selectedIds: Set<Int>,
    onMultipleSelected: (Int, Int, Int) -> Unit,
    onOneSelected: (Int) -> Unit,
    autoScrollSpeed: MutableState<Float>,
    autoScrollThreshold: Float
) = pointerInput(Unit) {

    // gets the item key (deck id in this case) at clicked position
    fun LazyGridState.gridItemKeyAtPosition(hitPoint: Offset): Int? =
        layoutInfo.visibleItemsInfo.find { itemInfo ->
            itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
        }?.key as? Int

    // initially long-clicked item and current item
    var initialKey: Int? = null
    var currentKey: Int? = null

    detectDragGesturesAfterLongPress(
        onDragStart = { offset ->
            // when long click on item, get it and add it to selection
            lazyGridState.gridItemKeyAtPosition(offset)?.let { key ->
                if (!selectedIds.contains(key)) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    initialKey = key
                    currentKey = key
                    onOneSelected(key)
                }
            }
        },
        onDragCancel = {
            initialKey = null
            autoScrollSpeed.value = 0f
        },
        onDragEnd = {
            initialKey = null
            autoScrollSpeed.value = 0f
        },
        onDrag = { change, _ ->
            if (initialKey == null) {
                return@detectDragGesturesAfterLongPress
            }

            val distFromBottom =
                lazyGridState.layoutInfo.viewportSize.height - change.position.y

            val distFromTop = change.position.y

            // determine autoScrollSpeed based on how far away
            // the finger is from the top or bottom of the screen
            autoScrollSpeed.value = when {
                distFromBottom < autoScrollThreshold -> autoScrollThreshold - distFromBottom
                distFromTop < autoScrollThreshold -> -(autoScrollThreshold - distFromTop)
                else -> 0f
            }

            lazyGridState.gridItemKeyAtPosition(change.position)?.let { key ->
                if (currentKey == null) {
                    return@let
                }

                // add items from initial item to current item to the selection
                onMultipleSelected(initialKey!!, currentKey!!, key)

                currentKey = key
            }
        }
    )
}