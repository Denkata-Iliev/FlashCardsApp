package com.example.flashcardsapp.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.NumberPicker
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcardsapp.R
import com.example.flashcardsapp.ui.DefaultTopBar
import com.example.flashcardsapp.ui.FlashCardAppViewModelProvider
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun SettingsScreen(
    onNavigateBackUp: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = FlashCardAppViewModelProvider.Factory)
) {
    val uiState by viewModel.settingsUiState
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val showDialogStandard = remember { mutableStateOf(false) }
    val showDialogTimed = remember { mutableStateOf(false) }
    val showDialogAdvanced = remember { mutableStateOf(false) }
    var showGoToSettingsDialog by remember { mutableStateOf(false) }

    val timerOptions = listOf(10, 12, 15)
    val selectedTime by rememberUpdatedState(uiState.notificationsLocalTime)
    var showTimePicker by remember { mutableStateOf(false) }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            viewModel.updateUiState(uiState.copy(notificationsEnabled = it, notificationPermGranted = it))
        }

    val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val notificationsEnabled = uiState.notificationsEnabled && uiState.notificationPermGranted

    val activity = LocalActivity.current

    var intentToEnableNotifications by remember { mutableStateOf(false) }
    NotificationPermissionWatcher(
        onPermissionChange = {
            viewModel.updateUiState(uiState.copy(notificationPermGranted = it))
            viewModel.updateSettings(uiState)
            if (intentToEnableNotifications) {
                viewModel.updateUiState(uiState.copy(notificationsEnabled = it))
                intentToEnableNotifications = false
            }
        }
    )

    if (showTimePicker) {
        TimePickerDialog(
            initialTime = selectedTime,
            onCancel = { showTimePicker = false },
            onConfirm = {
                viewModel.updateUiState(uiState.copy(notificationsTime = it.format(dateTimeFormatter)))
                showTimePicker = false
            }
        )
    }

    if (showGoToSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showGoToSettingsDialog = false },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider()
                    TextButton(
                        onClick = {
                            activity?.openAppSettings()
                            intentToEnableNotifications = true
                            showGoToSettingsDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.grant_permissions),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.notifications),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "You've permanently declined notifications permissions, " +
                            "so you need to manually grant them through phone settings."
                )
            }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.imePadding())
        },
        topBar = {
            DefaultTopBar(
                text = stringResource(R.string.settings),
                onNavigateBackUp = onNavigateBackUp
            )
        },
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
                    text = stringResource(R.string.standard_study_card_limit),
                    onValueChange = {
                        viewModel.updateUiState(uiState.copy(standardLimit = it))
                    },
                    selectedNumber = uiState.standardLimit,
                    range = SettingsDefaults.DEFAULT_RANGE,
                    showDialog = showDialogStandard
                )

                StudyCardLimitPicker(
                    text = stringResource(R.string.timed_study_card_limit),
                    onValueChange = {
                        viewModel.updateUiState(uiState.copy(timedLimit = it))
                    },
                    selectedNumber = uiState.timedLimit,
                    range = SettingsDefaults.DEFAULT_RANGE,
                    showDialog = showDialogTimed
                )

                StudyCardLimitPicker(
                    text = stringResource(R.string.advanced_study_card_limit),
                    onValueChange = {
                        viewModel.updateUiState(uiState.copy(advancedLimit = it))
                    },
                    selectedNumber = uiState.advancedLimit,
                    range = SettingsDefaults.DEFAULT_RANGE,
                    showDialog = showDialogAdvanced
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.study_timer),
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
                    Text(
                        text = stringResource(R.string.daily_notifications),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = {
                            // if Android < Tiramisu, I don't need permission for notifications
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                viewModel.updateUiState(uiState.copy(notificationsEnabled = it, notificationPermGranted = it))
                                return@Switch
                            }

                            // if permission granted, just update UI
                            if (uiState.notificationPermGranted) {
                                viewModel.updateUiState(uiState.copy(notificationsEnabled = it))
                                return@Switch
                            }

                            // if denied permanently, show dialog to go to app settings
                            if (activity != null && !activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                                showGoToSettingsDialog = true
                                return@Switch
                            }

                            // if not denied permanently, request permission
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    )
                }

                AnimatedVisibility(visible = notificationsEnabled) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(R.string.time),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Box(
                            modifier = Modifier
                                .clickable { showTimePicker = true }
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = selectedTime.format(dateTimeFormatter),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val selectedTimeParsed = selectedTime.format(dateTimeFormatter)
                        viewModel.updateUiState(uiState.copy(notificationsTime = selectedTimeParsed))
                        viewModel.updateSettings(uiState)

                        snackbarHostState.showSnackbar(
                            message = "Saved settings",
                            duration = SnackbarDuration.Short
                        )
                        onNavigateBackUp()
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = stringResource(R.string.save))
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

        Box(
            modifier = Modifier
                .clickable { showDialog.value = true }
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = selectedNumber.toString(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }
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
            Text(
                text = stringResource(R.string.select_time),
                style = MaterialTheme.typography.titleLarge
            )
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

@Composable
fun NotificationPermissionWatcher(onPermissionChange: (Boolean) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event != Lifecycle.Event.ON_RESUME) {
                return@LifecycleEventObserver
            }

            // if Android < Tiramisu, there's no need for permission
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                onPermissionChange(true)
                return@LifecycleEventObserver
            }

            // when screen is on stage ON_RESUME, check if notification permissions are granted
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            onPermissionChange(granted)
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
