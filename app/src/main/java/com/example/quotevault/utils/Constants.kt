package com.example.quotevault.utils



object Constants {
    // TODO: Replace with your actual Supabase credentials
    const val SUPABASE_URL =  "https://rbrwjrtxwmpqjmlttipg.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJicndqcnR4d21wcWptbHR0aXBnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjgyNzk2NTcsImV4cCI6MjA4Mzg1NTY1N30.PGaQxT-JuuOKtnc19YxP5FgSnoCCHlD9aAYetfUyEpo"

    // Categories
    val CATEGORIES = listOf(
        "All",
        "Motivation",
        "Love",
        "Success",
        "Wisdom",
        "Humor"
    )

    // Preferences
    const val PREFERENCES_NAME = "quote_vault_preferences"
    const val KEY_THEME_MODE = "theme_mode"
    const val KEY_ACCENT_COLOR = "accent_color"
    const val KEY_FONT_SIZE = "font_size"
    const val KEY_NOTIFICATION_TIME = "notification_time"
    const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"

    // Notification
    const val NOTIFICATION_CHANNEL_ID = "daily_quote_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Daily Quote"
    const val DAILY_QUOTE_NOTIFICATION_ID = 1001

    // Worker
    const val DAILY_QUOTE_WORK_NAME = "daily_quote_work"

    // Pagination
    const val PAGE_SIZE = 20
}