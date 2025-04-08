package com.example.flashcardsapp.ui.card

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcardsapp.R
import com.example.flashcardsapp.ui.DefaultTopBar
import com.example.flashcardsapp.ui.FlashCardAppViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun EditCardScreen(
    cardId: Int,
    onNavigateBackUp: () -> Unit,
    viewModel: EditCardViewModel = viewModel(factory = FlashCardAppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState = viewModel.cardUiState
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        viewModel.populateUiState(cardId)
    }

    Scaffold(
        topBar = {
            DefaultTopBar(
                text = stringResource(R.string.update_card),
                onNavigateBackUp = onNavigateBackUp
            )
        }
    ) { padding ->
        AddCardBody(
            uiState = uiState,
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    if (viewModel.updateCard(cardId)) {
                        onNavigateBackUp()
                    }
                }
            },
            focusRequester = focusRequester,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(dimensionResource(R.dimen.default_padding))
                .imePadding()
        )
    }
}