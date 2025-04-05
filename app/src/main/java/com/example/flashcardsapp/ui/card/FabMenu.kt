package com.example.flashcardsapp.ui.card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcardsapp.R

@Composable
fun FabMenu(
    items: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier
) {
    var fabMenuState by remember { mutableStateOf(FabMenuState.Collapsed) }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement =
            Arrangement.spacedBy(dimensionResource(R.dimen.default_padding), Alignment.Bottom),
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.default_padding))
    ) {
        FabMenuWrapper(
            expanded = fabMenuState == FabMenuState.Expanded,
            items = items,
        )

        FabMenuButton(
            state = fabMenuState,
            onClick = {
                fabMenuState = it
            }
        )
    }
}

@Composable
private fun FabMenuWrapper(
    expanded: Boolean,
    items: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier
) {
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Bottom,
            animationSpec = tween(150, easing = FastOutSlowInEasing)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(150, easing = FastOutSlowInEasing)
        )
    }

    val exitTransition = remember {
        shrinkVertically(
            shrinkTowards = Alignment.Bottom,
            animationSpec = tween(150, easing = FastOutSlowInEasing)
        ) + fadeOut(
            animationSpec = tween(150, easing = FastOutSlowInEasing)
        )
    }

    AnimatedVisibility(
        visible = expanded,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.default_padding)),
            modifier = modifier.fillMaxWidth()
        ) {
            items.forEach {
                it()
            }
        }
    }
}

@Composable
private fun FabMenuButton(
    state: FabMenuState,
    onClick: (FabMenuState) -> Unit,
    modifier: Modifier = Modifier
) {
    val nextMenuState = if (state == FabMenuState.Expanded) {
        FabMenuState.Collapsed
    } else {
        FabMenuState.Expanded
    }

    FloatingActionButton(
        onClick = { onClick(nextMenuState) },
        shape = CircleShape,
        modifier = modifier
    ) {
        Crossfade(targetState = state) { currentState ->
            // note that it's required to use the value passed by Crossfade
            // instead of your state value
            if (currentState == FabMenuState.Expanded) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "",
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "",
                )
            }
        }
    }
}

@Composable
fun FabMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        FabMenuItemLabel(text = label)

        FabMenuItemButton(
            icon = icon,
            onClick = onClick
        )
    }
}

@Composable
fun FabMenuItem(
    painter: Painter,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        FabMenuItemLabel(text = label)

        FabMenuItemButton(
            painter = painter,
            onClick = onClick
        )
    }
}

@Composable
private fun FabMenuItemLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corners_small)),
        color = Color.Black.copy(alpha = 0.8f),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            maxLines = 1,
            modifier = Modifier
                .padding(
                    horizontal = 20.dp,
                    vertical = 2.dp
                )
        )
    }
}

@Composable
private fun FabMenuItemButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape
    ) {
        Icon(
            imageVector = icon,
            contentDescription = ""
        )
    }
}

@Composable
private fun FabMenuItemButton(
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape
    ) {
        Icon(
            painter = painter,
            contentDescription = ""
        )
    }
}

enum class FabMenuState {
    Expanded,
    Collapsed
}