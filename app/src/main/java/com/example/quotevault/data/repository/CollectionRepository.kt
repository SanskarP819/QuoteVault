package com.example.quotevault.data.repository

import com.example.quotevault.data.remote.SupabaseClientWrapper
import com.example.quotevault.data.remote.dto.CollectionDto
import com.example.quotevault.data.remote.dto.CollectionItemDto
import com.example.quotevault.data.remote.dto.QuoteDto
import com.example.quotevault.data.remote.dto.toDomain
import com.example.quotevault.domain.model.Collection
import com.example.quotevault.domain.model.CollectionWithQuotes
import com.example.quotevault.domain.model.Quote
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CollectionRepository @Inject constructor(
    private val supabase: SupabaseClientWrapper
) {
    suspend fun getCollections(): Result<List<Collection>> = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.client.auth.currentUserOrNull()?.id
                ?: return@withContext Result.failure(Exception("User not logged in"))

            val collections = supabase.client.from("collections")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<CollectionDto>()
                .map { it.toDomain() }

            Result.success(collections)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCollectionWithQuotes(collectionId: String): Result<CollectionWithQuotes> =
        withContext(Dispatchers.IO) {
            try {
                val userId = supabase.client.auth.currentUserOrNull()?.id
                    ?: return@withContext Result.failure(Exception("User not logged in"))

                // Get collection
                val collectionDto = supabase.client.from("collections")
                    .select {
                        filter {
                            eq("id", collectionId)
                            eq("user_id", userId)
                        }
                    }
                    .decodeSingle<CollectionDto>()

                // Get collection items
                val items = supabase.client.from("collection_items")
                    .select {
                        filter {
                            eq("collection_id", collectionId)
                        }
                    }
                    .decodeList<CollectionItemDto>()

                val quoteIds = items.map { it.quoteId }

                val quotes = if (quoteIds.isNotEmpty()) {
                    supabase.client.from("quotes")
                        .select {
                            filter {
                                isIn("id", quoteIds)
                            }
                        }
                        .decodeList<QuoteDto>()
                        .map { it.toDomain() }
                } else {
                    emptyList()
                }

                Result.success(
                    CollectionWithQuotes(
                        collection = collectionDto.toDomain(),
                        quotes = quotes
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun createCollection(name: String, description: String?): Result<Collection> =
        withContext(Dispatchers.IO) {
            try {
                val userId = supabase.client.auth.currentUserOrNull()?.id
                    ?: return@withContext Result.failure(Exception("User not logged in"))

                val collection = supabase.client.from("collections")
                    .insert(
                        CollectionDto(
                            userId = userId,
                            name = name,
                            description = description
                        )
                    ) {
                        select()
                    }
                    .decodeSingle<CollectionDto>()

                Result.success(collection.toDomain())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun deleteCollection(collectionId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = supabase.client.auth.currentUserOrNull()?.id
                ?: return@withContext Result.failure(Exception("User not logged in"))

            supabase.client.from("collections")
                .delete {
                    filter {
                        eq("id", collectionId)
                        eq("user_id", userId)
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addQuoteToCollection(collectionId: String, quoteId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                supabase.client.from("collection_items")
                    .insert(
                        CollectionItemDto(
                            collectionId = collectionId,
                            quoteId = quoteId
                        )
                    )

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun removeQuoteFromCollection(collectionId: String, quoteId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                supabase.client.from("collection_items")
                    .delete {
                        filter {
                            eq("collection_id", collectionId)
                            eq("quote_id", quoteId)
                        }
                    }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
