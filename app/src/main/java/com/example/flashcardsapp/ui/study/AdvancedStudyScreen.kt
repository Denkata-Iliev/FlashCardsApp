package com.example.flashcardsapp.ui.study

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcardsapp.R
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.ui.CenteredText
import com.example.flashcardsapp.ui.CustomFactories
import com.example.flashcardsapp.ui.DefaultTopBar
import kotlinx.coroutines.delay

@Composable
fun AdvancedStudyScreen(
    deckId: Int,
    onNavigateBackUp: () -> Unit,
    viewModel: AdvancedStudyViewModel = viewModel(
        factory = CustomFactories.advancedStudyFactory(deckId)
    )
) {
    val uiState by viewModel.advancedUiState
    var card by remember { mutableStateOf<Card?>(null) }

    LaunchedEffect(uiState.cards) {
        if (card == null && uiState.cards.isNotEmpty()) {
            card = uiState.cards.random()
        }
    }

    Scaffold(
        topBar = {
            DefaultTopBar(onNavigateBackUp = onNavigateBackUp)
        }
    ) { padding ->
        var answerShown by remember { mutableStateOf(false) }
        var state by remember { mutableStateOf(CardFace.Front) }
        var shouldChangeCard by remember { mutableStateOf(false) }

        if (uiState.cards.isEmpty()) {
            CenteredText(
                text = stringResource(R.string.finished_advanced_session),
                padding = padding
            )

            return@Scaffold
        }

        if (card == null) {
            CenteredText(
                text = stringResource(R.string.could_not_retrieve_card),
                padding = padding
            )

            return@Scaffold
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            var userAnswer by remember { mutableStateOf("") }
            FlipCard(
                cardFace = state,
                axis = RotationAxis.AxisY,
                front = {
                    CardFrontInput(
                        text = card!!.question,
                        userAnswer = userAnswer,
                        onInputChange = { userAnswer = it }
                    )
                },
                back = {
                    CardText(text = card!!.answer)
                },
                modifier = Modifier
                    .weight(0.8f)
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(0.3f)
            ) {
                if (!answerShown) {
                    StudyButton(
                        text = stringResource(R.string.show_answer),
                        onClick = {
                            state = state.next
                            answerShown = true
                        },
                        enabled = userAnswer.trim().isNotEmpty()
                    )
                } else {
                    StudyButton(
                        text = stringResource(R.string.next),
                        onClick = {
                            state = state.next
                            shouldChangeCard = true
                            answerShown = false
                            userAnswer = ""
                        }
                    )
                }
            }
        }

        LaunchedEffect(state) {
            if (state == CardFace.Front && shouldChangeCard) {
                delay(100)
                viewModel.removeCardFromSession(card!!)
                card = uiState.cards.randomOrNull()
            }
        }
    }
}

@Composable
private fun CardFrontInput(
    text: String,
    userAnswer: String,
    onInputChange: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.default_padding))
            )
        }

        OutlinedTextField(
            value = userAnswer,
            onValueChange = onInputChange,
            placeholder = { Text(text = stringResource(R.string.type_your_answer)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { this.defaultKeyboardAction(ImeAction.Done) }
            ),
            maxLines = 2,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}