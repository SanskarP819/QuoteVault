package com.example.quotevault.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.data.local.PreferencesManager
import com.example.quotevault.domain.model.AccentColor
import com.example.quotevault.domain.model.FontSize
import com.example.quotevault.domain.model.ThemeMode
import com.example.quotevault.domain.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = preferencesManager.userPreferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesManager.updateThemeMode(mode)
        }
    }

    fun updateAccentColor(color: AccentColor) {
        viewModelScope.launch {
            preferencesManager.updateAccentColor(color)
        }
    }

    fun updateFontSize(size: FontSize) {
        viewModelScope.launch {
            preferencesManager.updateFontSize(size)
        }
    }

    fun updateNotificationTime(time: String) {
        viewModelScope.launch {
            preferencesManager.updateNotificationTime(time)
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateNotificationsEnabled(enabled)
        }
    }
}