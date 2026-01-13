package com.example.quotevault.data.remote.dto

import com.example.quotevault.domain.model.Collection
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

fun CollectionDto.toDomain(): Collection {
    return Collection(
        id = id ?: "",
        userId = userId,
        name = name,
        description = description,
        createdAt = createdAt ?: ""
    )
}

@Serializable
data class CollectionItemDto(
    @SerialName("id") val id: String? = null,
    @SerialName("collection_id") val collectionId: String,
    @SerialName("quote_id") val quoteId: String,
    @SerialName("created_at") val createdAt: String? = null
)