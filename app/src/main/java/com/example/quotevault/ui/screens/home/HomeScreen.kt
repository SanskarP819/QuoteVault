package com.example.quotevault.ui.screens.home


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.ui.components.AddToCollectionDialog
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.example.quotevault.ui.components.LoadingState
import com.example.quotevault.ui.components.QuoteCard
import com.example.quotevault.ui.components.QuoteDetailDialog
import com.example.quotevault.ui.components.ShareOptionsDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToBrowse: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToCollections: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var selectedQuote by remember { mutableStateOf<Quote?>(null) }
    var showShareDialog by remember { mutableStateOf(false) }
    var showCollectionDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QuoteVault") },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToBrowse,
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Browse") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToFavorites,
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("Favorites") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCollections,
                    icon = { Icon(Icons.Default.Collections, contentDescription = null) },
                    label = { Text("Collections") }
                )
            }
        }
    ) { padding ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(uiState.isLoading),
            onRefresh = { viewModel.loadData() }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Quote of the Day
                item {
                    if (uiState.quoteOfTheDay != null) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Quote of the Day",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            QuoteCard(
                                quote = uiState.quoteOfTheDay!!,
                                onQuoteClick = {
                                    selectedQuote = uiState.quoteOfTheDay
                                    showDetailDialog = true
                                },
                                onFavoriteClick = {
                                    viewModel.toggleFavorite(uiState.quoteOfTheDay!!)
                                },
                                onShareClick = {
                                    selectedQuote = uiState.quoteOfTheDay
                                    showShareDialog = true
                                },
                                onAddToCollectionClick = {
                                    selectedQuote = uiState.quoteOfTheDay
                                    showCollectionDialog = true
                                }
                            )
                        }
                    }
                }

                // Recent Quotes
                item {
                    Text(
                        text = "Recent Quotes",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                items(uiState.recentQuotes) { quote ->
                    QuoteCard(
                        quote = quote,
                        onQuoteClick = {
                            selectedQuote = quote
                            showDetailDialog = true
                        },
                        onFavoriteClick = { viewModel.toggleFavorite(quote) },
                        onShareClick = {
                            selectedQuote = quote
                            showShareDialog = true
                        },
                        onAddToCollectionClick = {
                            selectedQuote = quote
                            showCollectionDialog = true
                        }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showDetailDialog && selectedQuote != null) {
        QuoteDetailDialog(
            quote = selectedQuote!!,
            onDismiss = { showDetailDialog = false },
            onFavorite = {
                viewModel.toggleFavorite(selectedQuote!!)
            },
            onShare = {
                showDetailDialog = false
                showShareDialog = true
            },
            onAddToCollection = {
                showDetailDialog = false
                showCollectionDialog = true
            }
        )
    }

    if (showShareDialog && selectedQuote != null) {
        ShareOptionsDialog(
            quote = selectedQuote!!,
            onDismiss = { showShareDialog = false }
        )
    }

    if (showCollectionDialog && selectedQuote != null) {
        AddToCollectionDialog(
            quote = selectedQuote!!,
            onDismiss = { showCollectionDialog = false }
        )
    }
}