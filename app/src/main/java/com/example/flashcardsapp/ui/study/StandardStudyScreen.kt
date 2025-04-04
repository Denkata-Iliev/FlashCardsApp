package com.example.flashcardsapp.ui.study

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.graphicsLayer
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
fun StandardStudyScreen(
    deckId: Int,
    onNavigateBackUp: () -> Unit,
    viewModel: StandardStudyViewModel = viewModel(
        factory = CustomFactories.standardStudyFactory(deckId)
    )
) {
    val uiState by viewModel.standardUiState
    var card by remember { mutableStateOf<Card?>(null) }

    // Set the initial card only when the list is non-empty
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
                            contentDescription = stringResource(R.string.back_arrow),
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
                    text = stringResource(R.string.no_due_cards_for_session),
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
            FlipCard(
                cardFace = state,
                axis = RotationAxis.AxisY,
                front = {
                    CardText(card!!.question)
                },
                back = {
                    CardText(card!!.answer)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.CenterHorizontally)
                    .background(MaterialTheme.colorScheme.background)
            )

            if (answerShown) {
                AlgorithmButtonRow(
                    onRememberQualityClicked = {
                        viewModel.updateCard(card!!, it.recallScore)

                        state = state.next
                        shouldChangeCard = true
                        answerShown = false
                    }
                )

            } else {
                Button(
                    onClick = {
                        state = state.next
                        answerShown = true
                    }
                ) {
                    Text(text = stringResource(R.string.show_answer))
                }
            }

            // delay changing the card until the it is finished flipping back,
            // so the next answer isn't flashed to the user
            LaunchedEffect(state) {
                if (state == CardFace.Front && shouldChangeCard) {
                    delay(100)
                    viewModel.removeCardFromSession(card!!)
                    card = uiState.cards.randomOrNull()
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlgorithmButtonRow(
    onRememberQualityClicked: (RememberQuality) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        for (quality in RememberQuality.entries) {
            Button(
                onClick = { onRememberQualityClicked(quality) }
            ) {
                Text(text = quality.label)
            }
        }
    }
}

@Composable
fun CardText(text: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
            fontStyle = MaterialTheme.typography.bodyLarge.fontStyle
        )
    }
}

@Composable
fun FlipCard(
    cardFace: CardFace,
    modifier: Modifier = Modifier,
    axis: RotationAxis = RotationAxis.AxisY,
    back: @Composable () -> Unit = {},
    front: @Composable () -> Unit = {},
) {
    val rotation = animateFloatAsState(
        targetValue = cardFace.angle,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing,
        )
    )

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .shadow(dimensionResource(R.dimen.top_app_bar_elevation))
            .graphicsLayer {
                if (axis == RotationAxis.AxisX) {
                    rotationX = rotation.value
                } else {
                    rotationY = rotation.value
                }

                cameraDistance = 12f * density
            },
    ) {
        if (rotation.value <= 90f) {
            Box(modifier = Modifier.fillMaxSize()) {
                front()
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        if (axis == RotationAxis.AxisX) {
                            rotationX = 180f
                        } else {
                            rotationY = 180f
                        }
                    },
            ) {
                back()
            }
        }
    }
}

enum class CardFace(val angle: Float) {
    Front(0f) {
        override val next: CardFace
            get() = Back
    },

    Back(180f) {
        override val next: CardFace
            get() = Front
    };

    abstract val next: CardFace
}

enum class RotationAxis {
    AxisX,
    AxisY,
}

enum class RememberQuality(val recallScore: Int, val label: String) {
    VeryHard(1, "Very Hard"),
    Hard(2, "Hard"),
    Medium(3, "Medium"),
    Easy(4, "Easy"),
    TooEasy(5, "Too Easy")
}