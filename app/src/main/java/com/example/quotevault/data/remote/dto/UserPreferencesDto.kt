package com.example.quotevault.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPreferencesDto(
    @SerialName("user_id") val userId: String,
    @SerialName("theme_mode") val themeMode: String? = "system",
    @SerialName("accent_color") val accentColor: String? = "blue",
    @SerialName("font_size") val fontSize: String? = "medium",
    @SerialName("notification_time") val notificationTime: String? = "09:00",
    @SerialName("notifications_enabled") val notificationsEnabled: Boolean? = true,
    @SerialName("updated_at") val updatedAt: String? = null
)
