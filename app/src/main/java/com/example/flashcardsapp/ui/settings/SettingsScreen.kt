package com.example.flashcardsapp.ui.settings

import android.widget.NumberPicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcardsapp.R
import com.example.flashcardsapp.ui.DefaultTopBar
import com.example.flashcardsapp.ui.FlashCardAppViewModelProvider
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun SettingsScreen(
    onNavigateBackUp: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = FlashCardAppViewModelProvider.Factory)
) {
    val uiState by viewModel.settingsUiState

    val showDialogStandard = remember { mutableStateOf(false) }
    val showDialogTimed = remember { mutableStateOf(false) }
    val showDialogAdvanced = remember { mutableStateOf(false) }

    val timerOptions = listOf(10, 12, 15)
    val selectedTime = remember { mutableStateOf(LocalTime.now()) }
    val showTimePicker = remember { mutableStateOf(false) }

    if (showTimePicker.value) {
        TimePickerDialog(
            initialTime = selectedTime.value,
            onCancel = { showTimePicker.value = false },
            onConfirm = {
                selectedTime.value = it
                showTimePicker.value = false
            }
        )
    }

    Scaffold(
        topBar = { DefaultTopBar(onNavigateBackUp = onNavigateBackUp) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StudyCardLimitPicker(
                    text = "Standard Study Card Limit",
                    onValueChange = {
                        viewModel.updateUiState(uiState.copy(standardLimit = it))
                    },
                    selectedNumber = uiState.standardLimit,
                    range = 5..20,
                    showDialog = showDialogStandard
                )

                StudyCardLimitPicker(
                    text = "Timed Study Card Limit",
                    onValueChange = {
                        viewModel.updateUiState(uiState.copy(timedLimit = it))
                    },
                    selectedNumber = uiState.timedLimit,
                    range = 5..20,
                    showDialog = showDialogTimed
                )

                StudyCardLimitPicker(
                    text = "Advanced Study Card Limit",
                    onValueChange = {
                        viewModel.updateUiState(uiState.copy(advancedLimit = it))
                    },
                    selectedNumber = uiState.advancedLimit,
                    range = 5..20,
                    showDialog = showDialogAdvanced
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Study Timer",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    timerOptions.forEach { option ->
                        FilterChip(
                            selected = uiState.timerSeconds == option,
                            onClick = {
                                viewModel.updateUiState(uiState.copy(timerSeconds = option))
                            },
                            label = { Text(text = "$option") }
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daily Notifications", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = {
                            viewModel.updateUiState(uiState.copy(notificationsEnabled = it))
                        }
                    )
                }

                AnimatedVisibility(visible = uiState.notificationsEnabled) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Time", style = MaterialTheme.typography.bodyLarge)

                        Text(
                            text = selectedTime.value.format(DateTimeFormatter.ofPattern("HH:mm")),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable { showTimePicker.value = true },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {

                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
private fun StudyCardLimitPicker(
    onValueChange: (Int) -> Unit,
    selectedNumber: Int,
    showDialog: MutableState<Boolean>,
    range: IntRange,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        NumberSelector(
            text = text,
            value = selectedNumber,
            showDialog = showDialog,
            range = range,
            onValueChange = onValueChange,
        )
        Text(
            text = selectedNumber.toString(),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { showDialog.value = true },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: LocalTime,
    onCancel: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text("Select Time", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            TimeInput(state = state)
        },
        confirmButton = {
            TextButton(onClick = {
                val newTime = LocalTime.of(state.hour, state.minute)
                onConfirm(newTime)
            }) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun NumberSelector(
    value: Int,
    range: IntRange,
    showDialog: MutableState<Boolean>,
    onValueChange: (Int) -> Unit,
    text: String,
) {
    var number = value
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(text = text, style = MaterialTheme.typography.titleLarge)
            },
            text = {
                AndroidView(
                    factory = {
                        NumberPicker(it).apply {
                            minValue = range.first
                            maxValue = range.last
                            setOnValueChangedListener { _, _, newVal ->
                                number = newVal
                            }
                            number = this.value
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onValueChange(number)
                        showDialog.value = false
                    }
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }
}
