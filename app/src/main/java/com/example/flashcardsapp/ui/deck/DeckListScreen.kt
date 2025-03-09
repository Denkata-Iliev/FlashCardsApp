package com.example.flashcardsapp.ui.deck

import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcardsapp.R
import com.example.flashcardsapp.data.entity.Deck
import com.example.flashcardsapp.ui.FlashCardAppViewModelProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckListScreen(
    viewModel: DeckListViewModel = viewModel(factory = FlashCardAppViewModelProvider.Factory)
) {
    /**
     * TODO confirm dialog for deck deletion
     * TODO extract whatever possible to viewmodel or a separate Composable
     * TODO check code and see if there's anything else that needs to be done before commiting
     * TODO commit changes
     *
     */
    val coroutineScope = rememberCoroutineScope()
    var showDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    val deckListUiState by viewModel.deckListUiState.collectAsState()
    val selectedIds: MutableState<Set<Int>> = rememberSaveable { mutableStateOf(emptySet()) }
    val inSelectionMode = remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier
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
                    if (inSelectionMode.value) {
                        Row {
                            IconButton(
                                onClick = {
                                    inSelectionMode.value = false
                                    selectedIds.value = emptySet()
                                }
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
                    if (selectedIds.value.size == 1) {
                        IconButton(
                            onClick = {
                                Log.d("MINE", "Rename")
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.rename),
                                contentDescription = "Rename Icon",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    if (selectedIds.value.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.deleteByIds(selectedIds.value)
                                    selectedIds.value = emptySet()
                                    inSelectionMode.value = false
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Icon",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (inSelectionMode.value) {
                Row {
                    Column {
                        if (selectedIds.value.size != deckListUiState.decks.size) {
                            IconButton(
                                onClick = {
                                    val ids = deckListUiState.decks.map { it.id }.toSet()
                                    selectedIds.value = ids
                                },
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.radio_btn_unchecked),
                                    contentDescription = "Select All",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                )
                            }
                        }

                        if (selectedIds.value.size == deckListUiState.decks.size) {
                            IconButton(
                                onClick = {
                                    selectedIds.value = emptySet()
                                },
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
                        text = "${selectedIds.value.size} selected",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
            }

            DeckList(
                decks = deckListUiState.decks,
                contentPadding = PaddingValues(0.dp),
                selectedIds = selectedIds,
                inSelectionMode = inSelectionMode
            ) {
                inSelectionMode.value = it
            }
        }

        if (showDialog) {
            CreateDeckDialog(
                uiState = viewModel.createDeckUiState,
                onCancel = { showDialog = false },
                onCreate = {
                    viewModel.createDeck {
                        showDialog = false
                    }
                },
                onTextValueChange = { viewModel.updateCreateUiState(it) },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun DeckList(
    decks: List<Deck>,
    selectedIds: MutableState<Set<Int>>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    inSelectionMode: MutableState<Boolean>,
    onSelectionModeChange: (Boolean) -> Unit
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

    if (decks.isEmpty()) {
        Text(
            text = stringResource(R.string.no_decks_available),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(contentPadding)
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
                selectedIds = selectedIds
            )
    ) {
        items(items = decks, key = { it.id }) { deck ->
            val selected by remember { derivedStateOf { selectedIds.value.contains(deck.id) } }

            DeckItem(
                deck = deck,
                inSelectionMode = inSelectionMode.value,
                selected = selected,
                modifier = Modifier
                    .pointerInput(Unit) {
                        // on long press, enter selection mode
                        detectTapGestures(
                            onLongPress = {
                                if (!inSelectionMode.value) {
                                    inSelectionMode.value = true
                                    onSelectionModeChange(true)
                                    selectedIds.value += deck.id
                                }
                            }
                        )
                    }
                    .then(
                        if (inSelectionMode.value) {
                            // when in select mode, if the item is tapped,
                            // make it toggleable
                            Modifier.toggleable(
                                value = selected,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onValueChange = {
                                    if (it) {
                                        selectedIds.value += deck.id
                                    } else {
                                        selectedIds.value -= deck.id
                                    }
                                }
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
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.flash_card),
                contentDescription = stringResource(R.string.deck_icon_desc),
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp)
            )

            Text(
                text = deck.name,
                textAlign = TextAlign.Center
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
                    .padding(8.dp)
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
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun CreateDeckDialog(
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
            shape = RoundedCornerShape(16.dp)
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
                    Text(stringResource(R.string.create))
                }
            }
        }
    }
}

// defines how dragging works when in select mode
fun Modifier.selectDragHandler(
    lazyGridState: LazyGridState,
    haptics: HapticFeedback,
    selectedIds: MutableState<Set<Int>>,
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
                if (!selectedIds.value.contains(key)) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    initialKey = key
                    currentKey = key
                    selectedIds.value += key
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
                selectedIds.value = selectedIds.value
                    .minus(initialKey!!..currentKey!!)
                    .minus(currentKey!!..initialKey!!)
                    .plus(initialKey!!..key)
                    .plus(key..initialKey!!)

                currentKey = key
            }
        }
    )
}