package com.example.quotevault.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.ui.screens.collections.CollectionsViewModel
import com.example.quotevault.utils.QuoteCardStyle
import com.example.quotevault.utils.ShareHelper
import kotlinx.coroutines.launch

// Quote Detail Dialog
@Composable
fun QuoteDetailDialog(
    quote: Quote,
    onDismiss: () -> Unit,
    onFavorite: () -> Unit,
    onShare: () -> Unit,
    onAddToCollection: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = quote.category,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\"${quote.text}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "— ${quote.author}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = onFavorite) {
                    Icon(
                        imageVector = if (quote.isFavorite) Icons.Filled.Favorite
                        else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (quote.isFavorite) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }

                IconButton(onClick = onAddToCollection) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = "Add to collection")
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        }
    )
}

// Share Options Dialog
@Composable
fun ShareOptionsDialog(
    quote: Quote,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share Quote") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Share as text
                OutlinedButton(
                    onClick = {
                        ShareHelper.shareQuoteAsText(context, quote)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.TextFields, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share as Text")
                }

                Text(
                    text = "Or choose an image style:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Share as image - different styles
                QuoteCardStyle.values().forEach { style ->
                    OutlinedButton(
                        onClick = {
                            ShareHelper.shareQuoteAsImage(context, quote, style)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${style.name} Style")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Add to Collection Dialog
@Composable
fun AddToCollectionDialog(
    quote: Quote,
    onDismiss: () -> Unit,
    viewModel: CollectionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateNew by remember { mutableStateOf(false) }
    var newCollectionName by remember { mutableStateOf("") }
    var newCollectionDesc by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadCollections()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Collection") },
        text = {
            Column {
                if (!showCreateNew) {
                    if (uiState.collections.isEmpty()) {
                        Text("No collections yet. Create one!")
                    } else {
                        LazyColumn(
                            modifier = Modifier.height(200.dp)
                        ) {
                            items(uiState.collections) { collection ->
                                TextButton(
                                    onClick = {
                                        // Add quote to this collection
                                        kotlinx.coroutines.GlobalScope.launch {
                                            viewModel.addQuoteToCollection(collection.id, quote.id)
                                        }
                                        onDismiss()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(collection.name)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showCreateNew = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create New Collection")
                    }
                } else {
                    // Create new collection form
                    OutlinedTextField(
                        value = newCollectionName,
                        onValueChange = { newCollectionName = it },
                        label = { Text("Collection Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newCollectionDesc,
                        onValueChange = { newCollectionDesc = it },
                        label = { Text("Description (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showCreateNew = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Back")
                        }

                        Button(
                            onClick = {
                                viewModel.createCollectionAndAddQuote(
                                    newCollectionName,
                                    newCollectionDesc.ifBlank { null },
                                    quote.id
                                )
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            enabled = newCollectionName.isNotBlank()
                        ) {
                            Text("Create")
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!showCreateNew) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
