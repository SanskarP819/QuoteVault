package com.example.quotevault.domain.model

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accentColor: AccentColor = AccentColor.BLUE,
    val fontSize: FontSize = FontSize.MEDIUM,
    val notificationTime: String = "09:00",
    val notificationsEnabled: Boolean = true
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class AccentColor {
    BLUE, GREEN, PURPLE, ORANGE, RED
}

enum class FontSize(val value: Float) {
    SMALL(14f),
    MEDIUM(16f),
    LARGE(18f)
}