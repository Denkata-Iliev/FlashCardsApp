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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcardsapp.R
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.ui.CustomFactories
import kotlinx.coroutines.delay
import kotlin.math.max

@Composable
fun StandardStudyScreen(
    deckId: Int,
    viewModel: StandardStudyViewModel = viewModel(
        factory = CustomFactories.standardStudyFactory(
            deckId
        )
    )
) {
    val uiState by viewModel.standardUiState
    var card = uiState.cards.randomOrNull()

    Scaffold { padding ->
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
                Text(text = "You have no due cards in this deck")
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
                Text(text = "should mean done")
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

            fun flipCard() {
                state = state.next
                shouldChangeCard = true
                answerShown = false
            }

            fun nextCard(quality: RememberQuality) {
                val card1 = updateCard(card!!, quality.recallScore)
                viewModel.updateCard(card1)

                flipCard()
            }

            if (answerShown) {
                AlgorithmButtonRow(
                    onVeryHardClicked = { nextCard(it) },
                    onHardClicked = { nextCard(it) },
                    onMediumClicked = { nextCard(it) },
                    onEasyClicked = { nextCard(it) },
                    onTooEasyClicked = { nextCard(it) }
                )

            } else {
                Button(
                    onClick = {
                        state = state.next
                        answerShown = true
                    }
                ) { Text("Show Answer") }
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

fun updateCard(card: Card, recallScore: Int): Card {
    // all of the magic numbers are based on the official implementation of
    // the spaced repetition algorithm of SM-2: https://github.com/thyagoluciano/sm2

    // modification: instead of interval going from 1 to 6 days on the first and second review,
    // I've decided it'll be better if the cards appear a bit more often,
    // going from 12 to 24 to 48 hours on the first, second and third review respectively,
    // after which, the interval is based on the ease factor
    if (recallScore >= 3) {
        when (card.repCount) {
            0, 1 -> card.interval = 12.0
            2 -> card.interval = 24.0
            3 -> card.interval = 48.0
            else -> card.interval *= card.easeFactor
        }

        // Update the repetition count
        card.repCount++

        // Update ease factor based on recall score
        card.easeFactor += (0.1 - (5 - recallScore) * (0.08 + (5 - recallScore) * 0.02))
        card.easeFactor = max(1.3, card.easeFactor) // Prevent ease factor from going below 1.3
    } else {
        card.repCount = 0
        card.interval = 12.0
    }

    // Update the last reviewed timestamp
    card.lastReviewed = System.currentTimeMillis()

    return card
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlgorithmButtonRow(
    onVeryHardClicked: (RememberQuality) -> Unit,
    onHardClicked: (RememberQuality) -> Unit,
    onMediumClicked: (RememberQuality) -> Unit,
    onEasyClicked: (RememberQuality) -> Unit,
    onTooEasyClicked: (RememberQuality) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = { onVeryHardClicked(RememberQuality.VeryHard) }
        ) {
            Text(text = "Very Hard")
        }

        Button(
            onClick = { onHardClicked(RememberQuality.Hard) }
        ) {
            Text(text = "Hard")
        }

        Button(
            onClick = { onMediumClicked(RememberQuality.Medium) }
        ) {
            Text(text = "Medium")
        }

        Button(
            onClick = { onEasyClicked(RememberQuality.Easy) }
        ) {
            Text(text = "Easy")
        }

        Button(
            onClick = { onTooEasyClicked(RememberQuality.TooEasy) }
        ) {
            Text(text = "Too Easy")
        }
    }
}

@Composable
private fun CardText(text: String) {
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
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