package com.example.quotevault.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quotevault.domain.model.AccentColor
import com.example.quotevault.domain.model.FontSize
import com.example.quotevault.domain.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by viewModel.userPreferences.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showAccentDialog by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Appearance Section
            item {
                SectionHeader("Appearance")
            }

            item {
                SettingItem(
                    title = "Theme",
                    subtitle = preferences.themeMode.name.lowercase().replaceFirstChar { it.uppercase() },
                    onClick = { showThemeDialog = true }
                )
            }

            item {
                SettingItem(
                    title = "Accent Color",
                    subtitle = preferences.accentColor.name.lowercase().replaceFirstChar { it.uppercase() },
                    onClick = { showAccentDialog = true }
                )
            }

            item {
                SettingItem(
                    title = "Font Size",
                    subtitle = preferences.fontSize.name.lowercase().replaceFirstChar { it.uppercase() },
                    onClick = { showFontSizeDialog = true }
                )
            }

            // Notifications Section
            item {
                SectionHeader("Notifications")
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Quote Notification",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Get a new quote every day",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = preferences.notificationsEnabled,
                        onCheckedChange = { viewModel.updateNotificationsEnabled(it) }
                    )
                }
            }

            item {
                SettingItem(
                    title = "Notification Time",
                    subtitle = preferences.notificationTime,
                    onClick = { /* TODO: Show time picker */ },
                    enabled = preferences.notificationsEnabled
                )
            }
        }

        // Theme Dialog
        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                title = { Text("Select Theme") },
                text = {
                    Column {
                        ThemeMode.values().forEach { mode ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateThemeMode(mode)
                                        showThemeDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = preferences.themeMode == mode,
                                    onClick = {
                                        viewModel.updateThemeMode(mode)
                                        showThemeDialog = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(mode.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Accent Color Dialog
        if (showAccentDialog) {
            AlertDialog(
                onDismissRequest = { showAccentDialog = false },
                title = { Text("Select Accent Color") },
                text = {
                    Column {
                        AccentColor.values().forEach { color ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateAccentColor(color)
                                        showAccentDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = preferences.accentColor == color,
                                    onClick = {
                                        viewModel.updateAccentColor(color)
                                        showAccentDialog = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(color.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAccentDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Font Size Dialog
        if (showFontSizeDialog) {
            AlertDialog(
                onDismissRequest = { showFontSizeDialog = false },
                title = { Text("Select Font Size") },
                text = {
                    Column {
                        FontSize.values().forEach { size ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateFontSize(size)
                                        showFontSizeDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = preferences.fontSize == size,
                                    onClick = {
                                        viewModel.updateFontSize(size)
                                        showFontSizeDialog = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(size.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFontSizeDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            )
        }
    }
}
