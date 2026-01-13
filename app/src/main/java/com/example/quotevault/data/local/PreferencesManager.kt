package com.example.quotevault.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.quotevault.domain.model.AccentColor
import com.example.quotevault.domain.model.FontSize
import com.example.quotevault.domain.model.ThemeMode
import com.example.quotevault.domain.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val FONT_SIZE = stringPreferencesKey("font_size")
        val NOTIFICATION_TIME = stringPreferencesKey("notification_time")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val userPreferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            themeMode = ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: "SYSTEM"),
            accentColor = AccentColor.valueOf(prefs[Keys.ACCENT_COLOR] ?: "BLUE"),
            fontSize = FontSize.valueOf(prefs[Keys.FONT_SIZE] ?: "MEDIUM"),
            notificationTime = prefs[Keys.NOTIFICATION_TIME] ?: "09:00",
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true
        )
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }

    suspend fun updateAccentColor(color: AccentColor) {
        dataStore.edit { prefs ->
            prefs[Keys.ACCENT_COLOR] = color.name
        }
    }

    suspend fun updateFontSize(size: FontSize) {
        dataStore.edit { prefs ->
            prefs[Keys.FONT_SIZE] = size.name
        }
    }

    suspend fun updateNotificationTime(time: String) {
        dataStore.edit { prefs ->
            prefs[Keys.NOTIFICATION_TIME] = time
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.NOTIFICATIONS_ENABLED] = enabled
        }
    }
}