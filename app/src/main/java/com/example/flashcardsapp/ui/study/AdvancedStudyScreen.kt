package com.example.flashcardsapp.ui.study

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcardsapp.R
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.ui.CustomFactories
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
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
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
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
        var answerShown by remember { mutableStateOf(false) }
        var state by remember { mutableStateOf(CardFace.Front) }
        var shouldChangeCard by remember { mutableStateOf(false) }

        if (uiState.cards.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text(
                    text = stringResource(R.string.finished_advanced_session),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            return@Scaffold
        }

        if (card == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text(
                    text = stringResource(R.string.could_not_retrieve_card),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            return@Scaffold
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                modifier = Modifier
                    .weight(0.3f)
            ) {
                if (!answerShown) {
                    StudyButton(
                        text = stringResource(R.string.show_answer),
                        onClick = {
                            state = state.next
                            answerShown = true
                        }
                    )
                } else {
                    StudyButton(
                        text = stringResource(R.string.next),
                        onClick = {
                            state = state.next
                            shouldChangeCard = true
                            answerShown = false
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
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.default_padding))
        )

        OutlinedTextField(
            value = userAnswer,
            onValueChange = onInputChange,
            placeholder = { Text(text = stringResource(R.string.type_your_answer)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}