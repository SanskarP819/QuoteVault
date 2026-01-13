package com.example.quotevault

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.quotevault.data.repository.AuthRepository
import com.example.quotevault.domain.model.ThemeMode
import com.example.quotevault.ui.navigation.QuoteVaultNavigation
import com.example.quotevault.ui.navigation.Screen
import com.example.quotevault.ui.screens.settings.SettingsViewModel
import com.example.quotevault.ui.theme.QuoteVaultTheme
import com.example.quotevault.utils.NotificationHelper
import com.example.quotevault.workers.DailyQuoteWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    private var recoveryToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)
        DailyQuoteWorker.scheduleDailyWork(this)

        // Handle deep link
        recoveryToken = handleDeepLink(intent)

        setContent {
            QuoteVaultApp(
                authRepository = authRepository,
                recoveryToken = recoveryToken
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        recoveryToken = handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?): String? {
        val data = intent?.data ?: return null

        val type = data.getQueryParameter("type")
        val token = data.fragment?.substringAfter("access_token=")?.substringBefore("&")
            ?: data.getQueryParameter("token")

        return if (type == "recovery" && token != null) {
            lifecycleScope.launch {
                authRepository.verifyRecoveryToken(token)
            }
            token
        } else {
            null
        }
    }

    @Composable
    fun QuoteVaultApp(
        authRepository: AuthRepository,
        recoveryToken: String?,
        settingsViewModel: SettingsViewModel = hiltViewModel()
    ) {
        val preferences by settingsViewModel.userPreferences.collectAsState()
        val navController = rememberNavController()

        val startDestination = when {
            recoveryToken != null -> Screen.ResetPassword.route
            authRepository.isUserLoggedIn() -> Screen.Home.route
            else -> Screen.Login.route
        }

        val darkTheme = when (preferences.themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }

        QuoteVaultTheme(darkTheme = darkTheme) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                QuoteVaultNavigation(
                    navController = navController,
                    startDestination = startDestination
                )
            }
        }
    }
}