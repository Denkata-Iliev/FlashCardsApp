package com.example.flashcardsapp.ui.card

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcardsapp.R
import com.example.flashcardsapp.ui.FlashCardAppViewModelProvider

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
    viewModel: CardListViewModel = viewModel(factory = FlashCardAppViewModelProvider.Factory)
) {
    val cardListUiState by viewModel.cardListUiState(deckId).collectAsState()

    Scaffold(
        floatingActionButton = {
            FabMenu(
                listOf(
                    { FabMenuItem(
                        painter = painterResource(R.drawable.advanced_study_24px),
                        label = "Advanced Study",
                        onClick = onNavigateToAdvancedStudy
                    ) },

                    { FabMenuItem(
                        painter = painterResource(R.drawable.hourglass_bottom_24px),
                        label = "Timed Study",
                        onClick = onNavigateToTimedStudy
                    ) },

                    { FabMenuItem(
                        icon = Icons.Filled.PlayArrow,
                        label = "Standard Study",
                        onClick = onNavigateToStandardStudy
                    ) },

                    { FabMenuItem(
                        icon = Icons.Filled.Add,
                        label = "Add Cards",
                        onClick = onNavigateToAddCards
                    ) }
                )
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = cardListUiState.deckWithCards.deck.name) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBackUp
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_arrow)
                        )
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

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .clip(RoundedCornerShape(10.dp))
        ) {
            items(
                items = cardListUiState.deckWithCards.cards,
                key = { it.id }
            ) { card ->
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
                        Text(text = card.question)
                    },
                    trailingContent = {
                        IconButton(
                            onClick = { onNavigateToCard(card.id) }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.edit_icon)
                            )
                        }
                    },
                    tonalElevation = 3.dp,
                )

                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}