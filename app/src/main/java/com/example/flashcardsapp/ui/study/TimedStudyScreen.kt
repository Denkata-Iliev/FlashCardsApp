package com.example.flashcardsapp.ui.study

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcardsapp.R
import com.example.flashcardsapp.data.entity.Card
import com.example.flashcardsapp.ui.CustomFactories
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimedStudyScreen(
    deckId: Int,
    onNavigateBackUp: () -> Unit,
    viewModel: TimedStudyViewModel = viewModel(
        factory = CustomFactories.timedStudyFactory(deckId)
    )
) {
    val uiState by viewModel.timedUiState
    var card by remember { mutableStateOf<Card?>(null) }

    LaunchedEffect(uiState.cards) {
        if (card == null && uiState.cards.isNotEmpty()) {
            card = uiState.cards.random()
        }
    }

    val seconds by viewModel.seconds
    val progress by viewModel.progress

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
        var hasStarted by remember { mutableStateOf(false) }

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
                    text = stringResource(R.string.finished_timed_session),
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

        Crossfade(hasStarted) { currentState ->
            if (!currentState) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(onClick = { hasStarted = true }) {
                        Text(text = stringResource(R.string.start))
                    }
                }

                return@Crossfade
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 0.dp,
                        bottom = padding.calculateBottomPadding(),
                        start = padding.calculateLeftPadding(LayoutDirection.Ltr),
                        end = padding.calculateLeftPadding(LayoutDirection.Ltr),
                    ),
            ) {
                // when answer isn't shown,
                // meaning we're definitely on the question side of the card,
                // start the timer
                LaunchedEffect(answerShown) {
                    if (!answerShown) {
                        viewModel.startTimer()
                    }
                }

                CountdownTimer(
                    remainingSeconds = seconds / 1000L,
                    progress = progress,
                    strokeWidth = dimensionResource(R.dimen.timer_stroke_width),
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.timer_size)),
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.default_spacer_height)))

                FlipCard(
                    cardFace = state,
                    axis = RotationAxis.AxisY,
                    back = {
                        CardText(text = card!!.answer)
                    },
                    front = {
                        CardText(text = card!!.question)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .align(Alignment.CenterHorizontally)
                        .background(MaterialTheme.colorScheme.background)
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.default_spacer_height)))

                if (answerShown) {
                    StudyButton(
                        text = stringResource(R.string.next),
                        onClick = {
                            viewModel.resetTimer()

                            state = state.next
                            shouldChangeCard = true
                            answerShown = false
                        },
                    )
                } else {
                    StudyButton(
                        text = stringResource(R.string.show_answer),
                        onClick = {
                            viewModel.resetTimer()

                            state = state.next
                            answerShown = true
                        },
                    )
                }

                // when progress goes to 0,
                // meaning that the timer has run out (and has finished)
                // flip the card and reset the timer
                LaunchedEffect(progress) {
                    if (progress == 0f) {
                        answerShown = true
                        state = state.next
                        viewModel.resetTimer()
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
    }
}

@Composable
fun StudyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(0.6f)
    ) {
        Text(text = text)
    }
}

@Composable
fun CountdownTimer(
    progress: Float,
    modifier: Modifier = Modifier,
    remainingSeconds: Long = 10L,
    foregroundColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        // inactive background
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = backgroundColor,
            trackColor = Color.Unspecified,
            strokeWidth = strokeWidth
        )

        // active going back
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = foregroundColor,
            trackColor = Color.Unspecified,
            strokeWidth = strokeWidth
        )

        Text(
            text = "${remainingSeconds}s",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}