package com.example.quotevault.data.repository

import android.util.Log
import com.example.quotevault.data.remote.SupabaseClientWrapper
import com.example.quotevault.data.remote.dto.QuoteDto
import com.example.quotevault.data.remote.dto.toDomain
import com.example.quotevault.domain.model.Quote
import com.example.quotevault.utils.Constants
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuoteRepository @Inject constructor(
    private val supabase: SupabaseClientWrapper,
    private val favoriteRepository: FavoriteRepository
) {
    suspend fun getQuotes(
        category: String? = null,
        page: Int = 0,
        pageSize: Int = Constants.PAGE_SIZE
    ): Result<List<Quote>> = withContext(Dispatchers.IO) {
        try {
            val offset = page * pageSize
            val userId = supabase.client.auth.currentUserOrNull()?.id

            val quoteDtos = if (category != null && category != "All") {
                supabase.client.from("quotes")
                    .select(columns = Columns.ALL) {
                        filter {
                            eq("category", category)
                        }
                        limit(pageSize.toLong())
                        range(offset.toLong() until (offset + pageSize).toLong())
                    }
                    .decodeList<QuoteDto>()


            }
            else {
                supabase.client.from("quotes")
                    .select(columns = Columns.ALL) {
                        limit(pageSize.toLong())
                        range(offset.toLong() until (offset + pageSize).toLong())
                    }
                    .decodeList<QuoteDto>()
            }

            Log.d("SUPABASE_TEST", "Fetched quotes size = ${quoteDtos.size}")

            // Get favorite quote IDs
            val favoriteIds = if (userId != null) {
                favoriteRepository.getFavoriteQuoteIds().getOrNull() ?: emptySet()
            } else {
                emptySet()
            }

            val quotes = quoteDtos.map { dto ->
                dto.toDomain(isFavorite = favoriteIds.contains(dto.id))
            }

            Result.success(quotes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchQuotes(query: String): Result<List<Quote>> = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.client.auth.currentUserOrNull()?.id

            val quoteDtos = supabase.client.from("quotes")
                .select(columns = Columns.ALL) {
                    filter {
                        or {
                            ilike("text", "%$query%")
                            ilike("author", "%$query%")
                        }
                    }
                }
                .decodeList<QuoteDto>()

            val favoriteIds = if (userId != null) {
                favoriteRepository.getFavoriteQuoteIds().getOrNull() ?: emptySet()
            } else {
                emptySet()
            }

            val quotes = quoteDtos.map { dto ->
                dto.toDomain(isFavorite = favoriteIds.contains(dto.id))
            }

            Result.success(quotes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQuoteById(id: String): Result<Quote> = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.client.auth.currentUserOrNull()?.id

            val quoteDto = supabase.client.from("quotes")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingle<QuoteDto>()

            val isFavorite = if (userId != null) {
                favoriteRepository.isFavorite(id).getOrNull() ?: false
            } else {
                false
            }

            Result.success(quoteDto.toDomain(isFavorite))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRandomQuote(): Result<Quote> = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.client.auth.currentUserOrNull()?.id

            // Get a random quote using PostgreSQL's random() function
            val quotes = supabase.client.from("quotes")
                .select {
                    limit(1)
                }
                .decodeList<QuoteDto>()

            if (quotes.isEmpty()) {
                return@withContext Result.failure(Exception("No quotes found"))
            }

            val quoteDto = quotes.random()

            val isFavorite = if (userId != null) {
                favoriteRepository.isFavorite(quoteDto.id).getOrNull() ?: false
            } else {
                false
            }

            Result.success(quoteDto.toDomain(isFavorite))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}