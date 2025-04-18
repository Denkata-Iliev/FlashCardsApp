package com.example.flashcardsapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashcardsapp.notification.ReminderNotification
import com.example.flashcardsapp.ui.theme.FlashCardsAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    // field in class cuz it needs to be registered before STARTED
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val reminderNotification = ReminderNotification(applicationContext)
                reminderNotification.scheduleNotification()
            }

            lifecycleScope.launch {
                viewModel.markAskedForPermissionOnce(applicationContext, isGranted)
            }

            val showRationale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                false
            }

            viewModel.onPermissionResult(showRationale)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FlashCardsAppTheme {
                viewModel = viewModel()

                LaunchedEffect(Unit) {
                    viewModel.askForPermission(applicationContext, requestPermissionLauncher)
                }

                if (viewModel.shouldShowDialog) {
                    AlertDialog(
                        onDismissRequest = viewModel::dismissDialog,
                        confirmButton = {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                HorizontalDivider()
                                TextButton(
                                    onClick = viewModel::dismissDialog,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = stringResource(R.string.confirm),
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        },
                        title = {
                            Text(
                                text = "Notifications",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        text = {
                            Text(
                                text = "Memora sends you only one notification per day to remind you " +
                                        "to study. You can always enable notifications in settings."
                            )
                        },
                    )
                }

                FlashCardApp()
            }
        }
    }
}