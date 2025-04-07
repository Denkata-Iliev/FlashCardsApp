@file:OptIn(ExperimentalFoundationApi::class)

package com.example.flashcardsapp.ui.card

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcardsapp.R
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.ui.CustomFactories
import com.example.flashcardsapp.ui.deck.DeleteConfirmDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardListScreen(
    deckId: Int,
    onNavigateBackUp: () -> Unit,
    onNavigateToStandardStudy: () -> Unit,
    onNavigateToTimedStudy: () -> Unit,
    onNavigateToAdvancedStudy: () -> Unit,
    onNavigateToAddCards: () -> Unit,
    onNavigateToCard: (Int) -> Unit,
    viewModel: CardListViewModel = viewModel(factory = CustomFactories.cardListFactory(deckId))
) {
    val coroutineScope = rememberCoroutineScope()
    val cardListUiState by viewModel.cardListUiState.collectAsState()
    val inSelectionMode by viewModel.inSelectionMode.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()

    val showDeleteConfirm = viewModel.showDeleteConfirm

    Scaffold(
        floatingActionButton = {
            FabMenu(
                listOf(
                    {
                        FabMenuItem(
                            painter = painterResource(R.drawable.advanced_study_24px),
                            label = "Advanced Study",
                            onClick = onNavigateToAdvancedStudy
                        )
                    },

                    {
                        FabMenuItem(
                            painter = painterResource(R.drawable.hourglass_bottom_24px),
                            label = "Timed Study",
                            onClick = onNavigateToTimedStudy
                        )
                    },

                    {
                        FabMenuItem(
                            icon = Icons.Filled.PlayArrow,
                            label = "Standard Study",
                            onClick = onNavigateToStandardStudy
                        )
                    },

                    {
                        FabMenuItem(
                            icon = Icons.Filled.Add,
                            label = "Add Cards",
                            onClick = onNavigateToAddCards
                        )
                    }
                )
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = cardListUiState.deckWithCards.deck.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (!inSelectionMode) {
                                onNavigateBackUp()
                                return@IconButton
                            }

                            viewModel.exitSelectionMode()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_arrow)
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = inSelectionMode
                    ) {
                        Row {
                            IconButton(
                                onClick = viewModel::openDeleteConfirm
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.delete_icon)
                                )
                            }

                            Checkbox(
                                checked = selectedIds.size == cardListUiState.deckWithCards.cards.size,
                                onCheckedChange = {
                                    if (!it) {
                                        viewModel.deselectAll()
                                    } else {
                                        viewModel.selectAll(cardListUiState.deckWithCards.cards)
                                    }
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.shadow(elevation = dimensionResource(R.dimen.top_app_bar_elevation))
            )
        }
    ) { padding ->
        if (cardListUiState.deckWithCards.cards.isEmpty()) {
            Text(
                text = stringResource(R.string.no_cards_in_deck),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            )
            return@Scaffold
        }

        CardList(
            cards = cardListUiState.deckWithCards.cards,
            inSelectionMode = inSelectionMode,
            selectedIds = selectedIds,
            onNavigateToCard = onNavigateToCard,
            onSelectCard = viewModel::selectCard,
            onDeselectCard = viewModel::deselectCard,
            enterSelectionMode = viewModel::enterSelectionMode,
            padding = padding
        )

        AnimatedVisibility(visible = showDeleteConfirm) {
            DeleteConfirmDialog(
                text = "Are you sure you want to delete "
                        + if (selectedIds.size > 1) "these cards?" else "this card?",
                onDismiss = viewModel::closeDeleteConfirm,
                onCancel = viewModel::closeDeleteConfirm,
                onConfirm = {
                    coroutineScope.launch {
                        viewModel.delete(selectedIds)
                        viewModel.closeDeleteConfirm()
                        viewModel.exitSelectionMode()
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardList(
    cards: List<Card>,
    inSelectionMode: Boolean,
    selectedIds: Set<Int>,
    onNavigateToCard: (Int) -> Unit,
    onSelectCard: (Int) -> Unit,
    onDeselectCard: (Int) -> Unit,
    enterSelectionMode: () -> Unit,
    padding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .clip(RoundedCornerShape(10.dp))
    ) {
        items(
            items = cards,
            key = { it.id }
        ) { card ->
            val selected = selectedIds.contains(card.id)

            ListItem(
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.card_icon),
                        contentDescription = stringResource(R.string.card_icon),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_top_bar_size))
                    )
                },
                headlineContent = {
                    Text(
                        text = card.question,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                trailingContent = {
                    AnimatedContent(
                        targetState = inSelectionMode
                    ) { state ->
                        if (state) {
                            Checkbox(
                                checked = selected,
                                onCheckedChange = {
                                    if (it) {
                                        onSelectCard(card.id)
                                    } else {
                                        onDeselectCard(card.id)
                                    }
                                }
                            )
                        } else {
                            IconButton(
                                onClick = { onNavigateToCard(card.id) }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = stringResource(R.string.edit_icon)
                                )
                            }
                        }
                    }
                },
                tonalElevation = 3.dp,
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            if (!inSelectionMode) {
                                return@combinedClickable
                            }

                            if (selected) {
                                onDeselectCard(card.id)
                            } else {
                                onSelectCard(card.id)
                            }
                        },
                        onLongClick = {
                            if (inSelectionMode) {
                                return@combinedClickable
                            }

                            enterSelectionMode()
                            onSelectCard(card.id)
                        }
                    )
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }
    }
}