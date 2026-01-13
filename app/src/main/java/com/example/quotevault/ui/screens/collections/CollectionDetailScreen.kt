package com.example.quotevault.ui.screens.collections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quotevault.ui.components.EmptyState
import com.example.quotevault.ui.components.LoadingState
import com.example.quotevault.ui.components.QuoteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailScreen(
    collectionId: String,
    onNavigateBack: () -> Unit,
    viewModel: CollectionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.detailUiState.collectAsState()

    LaunchedEffect(collectionId) {
        viewModel.loadCollectionDetail(collectionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(uiState.collectionWithQuotes?.collection?.name ?: "Collection")
                },
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
            uiState.collectionWithQuotes == null -> EmptyState("Collection not found")
            uiState.collectionWithQuotes!!.quotes.isEmpty() -> {
                EmptyState("No quotes in this collection yet")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Collection description
                    item {
                        if (uiState.collectionWithQuotes!!.collection.description != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = uiState.collectionWithQuotes!!.collection.description!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }

                    // Quotes
                    items(uiState.collectionWithQuotes!!.quotes) { quote ->
                        QuoteCard(
                            quote = quote,
                            onFavoriteClick = { /* Already in collection */ },
                            onShareClick = { /* TODO: Share */ },
                            onAddToCollectionClick = {
                                viewModel.removeQuoteFromCollection(collectionId, quote.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
