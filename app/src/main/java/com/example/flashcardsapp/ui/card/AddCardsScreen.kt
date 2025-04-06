package com.example.flashcardsapp.ui.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcardsapp.R
import com.example.flashcardsapp.ui.FlashCardAppViewModelProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardsScreen(
    deckId: Int,
    onNavigateBackUp: () -> Unit,
    viewModel: AddCardsViewModel = viewModel(factory = FlashCardAppViewModelProvider.Factory)
) {
    val uiState = viewModel.cardUiState
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.add_cards_to_deck)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBackUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_arrow)
                        )
                    }
                },
                modifier = Modifier.shadow(elevation = dimensionResource(R.dimen.top_app_bar_elevation))
            )
        },
    ) { padding ->

        val focusRequester = remember { FocusRequester() }
        AddCardBody(
            uiState = uiState,
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    if (viewModel.addCard(deckId)) {
                        viewModel.requestFocus(focusRequester)
                        viewModel.displaySnackbar(snackbarHostState)
                    }
                }
            },
            focusRequester = focusRequester,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        )
    }
}

@Composable
fun AddCardBody(
    uiState: CardUiState,
    onValueChange: (CardUiState) -> Unit,
    onSaveClick: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
    ) {
        AddCardInputForm(
            uiState = uiState,
            onValueChange = onValueChange,
            onDone = onSaveClick,
            focusRequester = focusRequester
        )

        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth(fraction = 0.75f)
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}

@Composable
fun AddCardInputForm(
    uiState: CardUiState,
    onValueChange: (CardUiState) -> Unit,
    focusRequester: FocusRequester,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        val answerFocusRequester = FocusRequester()
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        OutlinedTextField(
            value = uiState.question,
            onValueChange = { onValueChange(uiState.copy(question = it)) },
            label = { Text(text = stringResource(R.string.question)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    answerFocusRequester.requestFocus()
                }
            ),
            modifier = modifier
                .focusRequester(focusRequester)
        )
        if (uiState.questionErrorMessage != null) {
            Text(
                text = uiState.questionErrorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        OutlinedTextField(
            value = uiState.answer,
            onValueChange = { onValueChange(uiState.copy(answer = it)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            ),
            label = { Text(text = stringResource(R.string.answer)) },
            modifier = modifier
                .focusRequester(answerFocusRequester)
        )
        if (uiState.answerErrorMessage != null) {
            Text(
                text = uiState.answerErrorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}