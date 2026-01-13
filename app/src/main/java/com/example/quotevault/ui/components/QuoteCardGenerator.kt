package com.example.quotevault.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.utils.QuoteCardStyle
import com.example.quotevault.utils.ShareHelper

@Composable
fun QuoteCardStylePicker(
    quote: Quote,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedStyle by remember { mutableStateOf(QuoteCardStyle.DEFAULT) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Card Style") },
        text = {
            Column {
                QuoteCardStyle.values().forEach { style ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStyle == style,
                            onClick = { selectedStyle = style }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(style.name)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    ShareHelper.shareQuoteAsImage(context, quote, selectedStyle)
                    onDismiss()
                }
            ) {
                Text("Share")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
