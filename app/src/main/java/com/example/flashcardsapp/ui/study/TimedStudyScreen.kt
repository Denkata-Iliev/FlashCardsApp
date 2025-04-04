package com.example.flashcardsapp.ui.study

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcardsapp.R
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
                            Modifier.clickable { onNavigateBackUp() }
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

        Crossfade(hasStarted) { currentState ->
            if (!currentState) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(onClick = { hasStarted = true }) {
                        Text(text = "Start")
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
                LaunchedEffect(answerShown) {
                    if (!answerShown) {
                        viewModel.startTimer()
                    }
                }

                CountdownTimer(
                    remainingSeconds = seconds / 1000L,
                    progress = progress,
                    strokeWidth = 8.dp,
                    modifier = Modifier.size(150.dp),
                )

                Spacer(modifier = Modifier.height(24.dp))

                FlipCard(
                    cardFace = state,
                    axis = RotationAxis.AxisY,
                    back = {
                        CardText(text = "answer")
                    },
                    front = {
                        CardText(text = "question")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.25f)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (answerShown) {
                    TimedStudyButton(
                        text = "Next",
                        onClick = {
                            viewModel.stopTimer()

                            state = state.next
                            shouldChangeCard = true
                            answerShown = false
                        },
                    )
                } else {
                    TimedStudyButton(
                        text = stringResource(R.string.show_answer),
                        onClick = {
                            viewModel.stopTimer()

                            state = state.next
                            answerShown = true
                        },
                    )
                }

                LaunchedEffect(state) {
                    if (state == CardFace.Front && shouldChangeCard) {
                        delay(100)
//                    viewModel.removeCardFromSession(card!!)
//                    card = uiState.cards.randomOrNull()
                    }
                }
            }
        }
    }
}

@Composable
fun TimedStudyButton(
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
            strokeWidth = strokeWidth
        )

        // active going back
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = foregroundColor,
            strokeWidth = strokeWidth
        )

        Text(
            text = "${remainingSeconds}s",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}