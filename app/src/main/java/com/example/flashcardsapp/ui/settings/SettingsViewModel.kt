package com.example.flashcardsapp.ui.settings

import android.content.Context
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

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsViewModel(private val applicationContext: Context) : ViewModel() {

    var settingsUiState = mutableStateOf(SettingsUiState())
        private set

    init {
        viewModelScope.launch {
            val prefs = applicationContext.dataStore.data.first()

            if (prefs[SettingsKeys.isInitializedKey] == true) {
                return@launch
            }

            applicationContext.dataStore.edit {
                it[SettingsKeys.standardLimitKey] = 10
                it[SettingsKeys.timedLimitKey] = 10
                it[SettingsKeys.advancedLimitKey] = 10
                it[SettingsKeys.timerSecondsKey] = 10
                it[SettingsKeys.notificationsEnabledKey] = false
                it[SettingsKeys.notificationsTimeKey] = "00:00"
                it[SettingsKeys.isInitializedKey] = true
            }
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
                it[SettingsKeys.notificationsTimeKey] = uiState.notificationsTime
            }
        }
    }

    fun updateUiState(uiState: SettingsUiState) {
        settingsUiState.value = uiState
    }

}

data class SettingsUiState(
    val standardLimit: Int = 10,
    val timedLimit: Int = 10,
    val advancedLimit: Int = 10,
    val timerSeconds: Int = 10,
    val notificationsEnabled: Boolean = false,
    val notificationsTime: String = "00:00",
)

object SettingsKeys {
    val standardLimitKey = intPreferencesKey("standard_limit")
    val timedLimitKey = intPreferencesKey("timed_limit")
    val advancedLimitKey = intPreferencesKey("advanced_limit")
    val timerSecondsKey = intPreferencesKey("timer_seconds")
    val notificationsEnabledKey = booleanPreferencesKey("notifications_enabled")
    val notificationsTimeKey = stringPreferencesKey("notifications_time")
    val hasRequestPermissionOnceKey = booleanPreferencesKey("has_requested_permission_once")
    val isInitializedKey = booleanPreferencesKey("is_initialized")
}
