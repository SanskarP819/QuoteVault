package com.example.quotevault.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.quotevault.R
import com.example.quotevault.data.remote.SupabaseClientWrapper
import com.example.quotevault.data.repository.FavoriteRepository
import com.example.quotevault.data.repository.QuoteRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuoteWidgetReceiver : AppWidgetProvider() {

    @Inject
    lateinit var quoteRepository: QuoteRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        scope.launch {
            try {
                val result = quoteRepository.getRandomQuote()

                result.fold(
                    onSuccess = { quote ->
                        val views = RemoteViews(context.packageName, R.layout.widget_quote).apply {
                            setTextViewText(R.id.widget_quote_text, "\"${quote.text}\"")
                            setTextViewText(R.id.widget_quote_author, "— ${quote.author}")
                        }

                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    },
                    onFailure = {
                        val views = RemoteViews(context.packageName, R.layout.widget_quote).apply {
                            setTextViewText(R.id.widget_quote_text, "Quote of the Day")
                            setTextViewText(R.id.widget_quote_author, "Open app to see quote")
                        }

                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        job.cancel()
    }
}
