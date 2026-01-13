package com.example.quotevault.data.repository

import com.example.quotevault.data.remote.SupabaseClientWrapper


import com.example.quotevault.data.remote.dto.QuoteDto
import com.example.quotevault.data.remote.dto.UserFavoriteDto
import com.example.quotevault.data.remote.dto.toDomain
import com.example.quotevault.domain.model.Quote
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(
    private val supabase: SupabaseClientWrapper
) {
    suspend fun getFavorites(): Result<List<Quote>> = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.client.auth.currentUserOrNull()?.id
                ?: return@withContext Result.failure(Exception("User not logged in"))

            // Get all favorite quote IDs for this user
            val favorites = supabase.client.from("user_favorites")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<UserFavoriteDto>()

            val quoteIds = favorites.map { it.quoteId }

            if (quoteIds.isEmpty()) {
                return@withContext Result.success(emptyList())
            }

            // Get the actual quotes
            val quotes = supabase.client.from("quotes")
                .select {
                    filter {
                        isIn("id", quoteIds)
                    }
                }
                .decodeList<QuoteDto>()
                .map { it.toDomain(isFavorite = true) }

            Result.success(quotes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFavorite(quoteId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.client.auth.currentUserOrNull()?.id
                ?: return@withContext Result.failure(Exception("User not logged in"))

            supabase.client.from("user_favorites")
                .insert(
                    UserFavoriteDto(
                        userId = userId,
                        quoteId = quoteId
                    )
                )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFavorite(quoteId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.client.auth.currentUserOrNull()?.id
                ?: return@withContext Result.failure(Exception("User not logged in"))

            supabase.client.from("user_favorites")
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("quote_id", quoteId)
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isFavorite(quoteId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.client.auth.currentUserOrNull()?.id
                ?: return@withContext Result.success(false)

            val favorites = supabase.client.from("user_favorites")
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("quote_id", quoteId)
                    }
                }
                .decodeList<UserFavoriteDto>()

            Result.success(favorites.isNotEmpty())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavoriteQuoteIds(): Result<Set<String>> = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.client.auth.currentUserOrNull()?.id
                ?: return@withContext Result.success(emptySet())

            val favorites = supabase.client.from("user_favorites")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<UserFavoriteDto>()

            Result.success(favorites.map { it.quoteId }.toSet())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
