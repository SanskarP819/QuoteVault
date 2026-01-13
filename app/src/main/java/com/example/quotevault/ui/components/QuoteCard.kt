package com.example.quotevault.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.quotevault.domain.model.Quote

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteCard(
quote: Quote,
onQuoteClick: () -> Unit = {},
onFavoriteClick: () -> Unit,
onShareClick: () -> Unit,
onAddToCollectionClick: () -> Unit,
modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onQuoteClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "\"${quote.text}\"",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "— ${quote.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = quote.category,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Row {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (quote.isFavorite) Icons.Filled.Favorite
                            else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (quote.isFavorite) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onAddToCollectionClick) {
                        Icon(
                            imageVector = Icons.Default.BookmarkAdd,
                            contentDescription = "Add to collection"
                        )
                    }

                    IconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                }
            }
        }
    }
}










//fun QuoteCard(
//    quote: Quote,
//    onFavoriteClick: () -> Unit,
//    onShareClick: () -> Unit,
//    onAddToCollectionClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Text(
//                text = "\"${quote.text}\"",
//                style = MaterialTheme.typography.bodyLarge,
//                maxLines = 4,
//                overflow = TextOverflow.Ellipsis
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column {
//                    Text(
//                        text = "— ${quote.author}",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//
//                    Chip(
//                        onClick = { },
//                        label = { Text(quote.category) }
//                    )
//                }
//
//                Row {
//                    IconButton(onClick = onFavoriteClick) {
//                        Icon(
//                            imageVector = if (quote.isFavorite) Icons.Filled.Favorite
//                            else Icons.Filled.FavoriteBorder,
//                            contentDescription = "Favorite",
//                            tint = if (quote.isFavorite) MaterialTheme.colorScheme.primary
//                            else MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }
//
//                    IconButton(onClick = onAddToCollectionClick) {
//                        Icon(
//                            imageVector = Icons.Default.BookmarkAdd,
//                            contentDescription = "Add to collection"
//                        )
//                    }
//
//                    IconButton(onClick = onShareClick) {
//                        Icon(
//                            imageVector = Icons.Default.Share,
//                            contentDescription = "Share"
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun Chip(
//    onClick: () -> Unit,
//    label: @Composable () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Surface(
//        onClick = onClick,
//        modifier = modifier,
//        shape = MaterialTheme.shapes.small,
//        color = MaterialTheme.colorScheme.primaryContainer
//    ) {
//        Box(
//            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            label()
//        }
//    }
//}