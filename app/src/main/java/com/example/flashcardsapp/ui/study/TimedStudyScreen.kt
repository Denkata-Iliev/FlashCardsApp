package com.example.flashcardsapp.ui.study

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
import com.example.flashcardsapp.ui.theme.FlashCardsAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimedStudyScreen(
    deckId: Int,
    onNavigateBackUp: () -> Unit,
    viewModel: TimedStudyViewModel = viewModel(
        factory = CustomFactories.timedStudyFactory(deckId)
    )
) {
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
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 0.dp,
                    bottom = padding.calculateBottomPadding().plus(80.dp),
                    start = padding.calculateLeftPadding(LayoutDirection.Ltr),
                    end = padding.calculateLeftPadding(LayoutDirection.Ltr),
                ),
        ) {
            CountdownTimer(
                progress = .5f,
                strokeWidth = 8.dp,
                modifier = Modifier.size(150.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            FlipCard(
                cardFace = CardFace.Front,
                axis = RotationAxis.AxisX,
                back = { /* Back Content */ },
                front = {
                    CardText(text = "questionText")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.25f)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(stringResource(R.string.show_answer))
            }
        }
    }
}

@Composable
fun CountdownTimer(
    progress: Float,
    modifier: Modifier = Modifier,
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
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun t() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(
                        onClick = {  }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_arrow),
                            Modifier.clickable {  }
                        )
                    }
                },
                modifier = Modifier.shadow(elevation = dimensionResource(R.dimen.top_app_bar_elevation))
            )
        }
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 0.dp,
                    bottom = padding.calculateBottomPadding().plus(80.dp),
                    start = padding.calculateLeftPadding(LayoutDirection.Ltr),
                    end = padding.calculateLeftPadding(LayoutDirection.Ltr),
                ),
        ) {
            CountdownTimer(
                progress = .5f,
                strokeWidth = 8.dp,
                modifier = Modifier.size(150.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            FlipCard(
                cardFace = CardFace.Front,
                axis = RotationAxis.AxisX,
                back = { /* Back Content */ },
                front = {
                    CardText(text = "questionText")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.25f)
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(stringResource(R.string.show_answer))
            }
        }
    }

}