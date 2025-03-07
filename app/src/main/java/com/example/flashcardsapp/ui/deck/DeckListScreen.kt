package com.example.flashcardsapp.ui.deck

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcardsapp.R
import com.example.flashcardsapp.data.entity.Deck
import com.example.flashcardsapp.ui.FlashCardAppViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun DeckListScreen(
    viewModel: DeckListViewModel = viewModel(factory = FlashCardAppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    val deckListUiState by viewModel.deckListUiState.collectAsState()

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

                if (showDialog) {
                    CreateDeckDialog(
                        uiState = viewModel.createDeckUiState,
                        onCancel = { showDialog = false },
                        onCreate = {
                            coroutineScope.launch {
                                viewModel.createDeck()
                                showDialog = false
                            }
                        },
                        onTextValueChange = { viewModel.updateCreateUiState(it) },
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    ) { padding ->
        DeckList(
            decks = deckListUiState.decks,
            contentPadding = padding
        )
    }
}

@Composable
fun DeckList(
    decks: List<Deck>,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
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
        columns = GridCells.Adaptive(75.dp),
    ) {
        items(items = decks, key = { it.id }) { deck ->
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
        }
    }
}

@Composable
fun CreateDeckDialog(
    uiState: CreateDeckUiState,
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onCreate: () -> Unit,
    onTextValueChange: (String) -> Unit
) {
    Dialog(
        onDismissRequest = {}
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.deckName,
                onValueChange = onTextValueChange,
                label = { Text(stringResource(R.string.deck_name_hint)) },
                modifier = modifier.fillMaxWidth()
            )

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