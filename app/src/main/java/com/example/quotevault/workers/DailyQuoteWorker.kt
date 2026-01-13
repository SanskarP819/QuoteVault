package com.example.quotevault.workers

import android.content.Context
import androidx.hilt.work.HiltWorker

import androidx.work.*
import com.example.quotevault.data.remote.SupabaseClientWrapper
import com.example.quotevault.data.repository.QuoteRepository
import com.example.quotevault.data.repository.FavoriteRepository
import com.example.quotevault.utils.Constants
import com.example.quotevault.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltWorker
class DailyQuoteWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val quoteRepository: QuoteRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Get random quote
            val result = quoteRepository.getRandomQuote()

            result.fold(
                onSuccess = { quote ->
                    // Show notification
                    NotificationHelper.showQuoteNotification(applicationContext, quote)
                    Result.success()
                },
                onFailure = {
                    Result.retry()
                }
            )
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        fun scheduleDailyWork(context: Context) {
            // Calculate initial delay to 9 AM
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)

                // If it's past 9 AM, schedule for tomorrow
                if (before(currentDate)) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

            val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyQuoteWorker>(
                1, TimeUnit.DAYS
            )
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                Constants.DAILY_QUOTE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWorkRequest
            )
        }

        fun cancelDailyWork(context: Context) {
            WorkManager.getInstance(context)
                .cancelUniqueWork(Constants.DAILY_QUOTE_WORK_NAME)
        }
    }
}