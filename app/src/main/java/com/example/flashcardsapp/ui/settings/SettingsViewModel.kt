package com.example.flashcardsapp.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsViewModel(private val applicationContext: Context) : ViewModel() {

    var settingsUiState = mutableStateOf(SettingsUiState())
        private set

    init {
        viewModelScope.launch {
            val prefs = applicationContext.dataStore.data.first()

            if (prefs[SettingsKeys.isInitializedKey] == false) {
                applicationContext.dataStore.edit {
                    it[SettingsKeys.standardLimitKey] = SettingsDefaults.DEFAULT_STUDY_CARD_LIMIT
                    it[SettingsKeys.timedLimitKey] = SettingsDefaults.DEFAULT_STUDY_CARD_LIMIT
                    it[SettingsKeys.advancedLimitKey] = SettingsDefaults.DEFAULT_STUDY_CARD_LIMIT
                    it[SettingsKeys.timerSecondsKey] = SettingsDefaults.DEFAULT_TIMER_SECONDS
                    it[SettingsKeys.notificationsTimeKey] = SettingsDefaults.DEFAULT_NOTIFICATIONS_TIME
                    it[SettingsKeys.isInitializedKey] = true
                }
            }

            settingsUiState.value = SettingsUiState(
                standardLimit = prefs[SettingsKeys.standardLimitKey] ?: SettingsDefaults.DEFAULT_STUDY_CARD_LIMIT,
                timedLimit = prefs[SettingsKeys.timedLimitKey] ?: SettingsDefaults.DEFAULT_STUDY_CARD_LIMIT,
                advancedLimit = prefs[SettingsKeys.advancedLimitKey] ?: SettingsDefaults.DEFAULT_STUDY_CARD_LIMIT,
                timerSeconds = prefs[SettingsKeys.timerSecondsKey] ?: SettingsDefaults.DEFAULT_TIMER_SECONDS,
                notificationsEnabled = prefs[SettingsKeys.notificationsEnabledKey] ?: false,
                notificationPermGranted = prefs[SettingsKeys.notificationPermissionGrantedKey] ?: false,
                notificationsTime = prefs[SettingsKeys.notificationsTimeKey]
                    ?: SettingsDefaults.DEFAULT_NOTIFICATIONS_TIME
            )
        }
    }

    fun updateSettings(uiState: SettingsUiState) {
        viewModelScope.launch {
            applicationContext.dataStore.edit {
                it[SettingsKeys.standardLimitKey] = uiState.standardLimit
                it[SettingsKeys.timedLimitKey] = uiState.timedLimit
                it[SettingsKeys.advancedLimitKey] = uiState.advancedLimit
                it[SettingsKeys.timerSecondsKey] = uiState.timerSeconds
                it[SettingsKeys.notificationsEnabledKey] = uiState.notificationsEnabled
                it[SettingsKeys.notificationPermissionGrantedKey] = uiState.notificationPermGranted
                it[SettingsKeys.notificationsTimeKey] = uiState.notificationsTime
            }
        }
    }

    fun updateUiState(uiState: SettingsUiState) {
        settingsUiState.value = uiState
    }

    companion object {
        val DEFAULT_RANGE = 5..20
        const val DEFAULT_STUDY_CARD_LIMIT = 10
        private const val DEFAULT_TIMER_SECONDS = 10
        private const val DEFAULT_NOTIFICATIONS_TIME = "09:00"
    }
}

data class SettingsUiState(
    val standardLimit: Int = SettingsDefaults.DEFAULT_STUDY_CARD_LIMIT,
    val timedLimit: Int = SettingsDefaults.DEFAULT_STUDY_CARD_LIMIT,
    val advancedLimit: Int = SettingsDefaults.DEFAULT_STUDY_CARD_LIMIT,
    val timerSeconds: Int = SettingsDefaults.DEFAULT_TIMER_SECONDS,
    val notificationsEnabled: Boolean = false,
    val notificationPermGranted: Boolean = false,
    val notificationsTime: String = SettingsDefaults.DEFAULT_NOTIFICATIONS_TIME,
) {
    val notificationsLocalTime: LocalTime
        get() {
            val notificationTimeSplit = notificationsTime.split(':')
            return LocalTime.of(notificationTimeSplit[0].toInt(), notificationTimeSplit[1].toInt())
        }
}

object SettingsKeys {
    val standardLimitKey = intPreferencesKey("standard_limit")
    val timedLimitKey = intPreferencesKey("timed_limit")
    val advancedLimitKey = intPreferencesKey("advanced_limit")
    val timerSecondsKey = intPreferencesKey("timer_seconds")
    val notificationsEnabledKey = booleanPreferencesKey("notifications_enabled")
    val notificationsTimeKey = stringPreferencesKey("notifications_time")
    val hasRequestPermissionOnceKey = booleanPreferencesKey("has_requested_permission_once")
    val isInitializedKey = booleanPreferencesKey("is_initialized")
    val notificationPermissionGrantedKey = booleanPreferencesKey("notification_permission_granted")
}

object SettingsDefaults {
    val DEFAULT_RANGE = 5..20
    const val DEFAULT_STUDY_CARD_LIMIT = 10
    const val DEFAULT_TIMER_SECONDS = 10
    const val DEFAULT_NOTIFICATIONS_TIME = "09:00"
}

fun Activity.openAppSettings() {
    Intent().apply {
        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    }
        .also(::startActivity)
}
