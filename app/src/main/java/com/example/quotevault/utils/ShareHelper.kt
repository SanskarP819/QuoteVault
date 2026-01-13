package com.example.quotevault.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.quotevault.domain.model.Quote
import java.io.File
import java.io.FileOutputStream

object ShareHelper {

    fun shareQuoteAsText(context: Context, quote: Quote) {
        val shareText = "\"${quote.text}\"\n\n— ${quote.author}"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share Quote")
        context.startActivity(shareIntent)
    }

    fun shareQuoteAsImage(context: Context, quote: Quote, style: QuoteCardStyle = QuoteCardStyle.DEFAULT) {
        val bitmap = generateQuoteImage(quote, style)
        val uri = saveBitmapToCache(context, bitmap)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/jpeg"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Quote Image"))
    }

    private fun generateQuoteImage(quote: Quote, style: QuoteCardStyle): Bitmap {
        val width = 1080
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background
        canvas.drawColor(style.backgroundColor)

        // Setup paint for text
        val textPaint = Paint().apply {
            color = style.textColor
            textSize = 48f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        val authorPaint = Paint().apply {
            color = style.authorColor
            textSize = 36f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        // Draw quote text (word wrap)
        val padding = 100
        val maxWidth = width - (padding * 2)
        val words = quote.text.split(" ")
        var line = ""
        var y = height / 2 - 100

        for (word in words) {
            val testLine = if (line.isEmpty()) word else "$line $word"
            val bounds = Rect()
            textPaint.getTextBounds(testLine, 0, testLine.length, bounds)

            if (bounds.width() > maxWidth && line.isNotEmpty()) {
                canvas.drawText(line, width / 2f, y.toFloat(), textPaint)
                line = word
                y += 70
            } else {
                line = testLine
            }
        }
        canvas.drawText(line, width / 2f, y.toFloat(), textPaint)

        // Draw author
        canvas.drawText("— ${quote.author}", width / 2f, (y + 120).toFloat(), authorPaint)

        return bitmap
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()

        val file = File(cachePath, "quote_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}

enum class QuoteCardStyle(
    val backgroundColor: Int,
    val textColor: Int,
    val authorColor: Int
) {
    DEFAULT(
        backgroundColor = Color.parseColor("#1976D2"),
        textColor = Color.WHITE,
        authorColor = Color.parseColor("#BBDEFB")
    ),
    DARK(
        backgroundColor = Color.parseColor("#212121"),
        textColor = Color.WHITE,
        authorColor = Color.parseColor("#BDBDBD")
    ),
    LIGHT(
        backgroundColor = Color.WHITE,
        textColor = Color.parseColor("#212121"),
        authorColor = Color.parseColor("#757575")
    ),
    GRADIENT(
        backgroundColor = Color.parseColor("#667eea"),
        textColor = Color.WHITE,
        authorColor = Color.parseColor("#E0E7FF")
    )
}