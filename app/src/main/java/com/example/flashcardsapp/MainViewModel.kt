package com.example.flashcardsapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import com.example.flashcardsapp.ui.settings.SettingsKeys
import com.example.flashcardsapp.ui.settings.dataStore
import kotlinx.coroutines.flow.first

class MainViewModel : ViewModel() {
    var shouldShowDialog by mutableStateOf(false)
        private set

    fun dismissDialog() {
        shouldShowDialog = false
    }

    fun onPermissionResult(shouldShowRationale: Boolean) {
        shouldShowDialog = shouldShowRationale
    }

    suspend fun askForPermission(
        applicationContext: Context,
        requestPermissionLauncher: ActivityResultLauncher<String>
    ) {
        applicationContext.dataStore.edit {
            if (!it.contains(SettingsKeys.hasRequestPermissionOnceKey)) {
                it[SettingsKeys.hasRequestPermissionOnceKey] = false
            }
        }

        // check if it has already been asked for permissions and if not, then that's the first time
        // the app has been started, so ask for permissions
        if (applicationContext.dataStore.data.first()[SettingsKeys.hasRequestPermissionOnceKey] == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    suspend fun markAskedForPermissionOnce(applicationContext: Context, isGranted: Boolean) {
        applicationContext.dataStore.edit { prefs ->
            prefs[SettingsKeys.hasRequestPermissionOnceKey] = true
            prefs[SettingsKeys.notificationsEnabledKey] = isGranted
        }
    }
}