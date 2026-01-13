package com.example.quotevault.ui.screens.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quotevault.ui.components.EmptyState
import com.example.quotevault.ui.components.LoadingState
import com.example.quotevault.ui.components.QuoteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateBack: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingState()
            uiState.favorites.isEmpty() -> EmptyState("No favorite quotes yet")
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(uiState.favorites) { quote ->
                        QuoteCard(
                            quote = quote,
                            onFavoriteClick = { viewModel.removeFavorite(quote) },
                            onShareClick = { /* TODO: Share */ },
                            onAddToCollectionClick = { /* TODO: Add to collection */ }
                        )
                    }
                }
            }
        }
    }
}