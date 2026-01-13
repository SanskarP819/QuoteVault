package com.example.quotevault.data.remote.dto


import com.example.quotevault.domain.model.Quote
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuoteDto(
    @SerialName("id") val id: String,
    @SerialName("text") val text: String,
    @SerialName("author") val author: String,
    @SerialName("category") val category: String,
    @SerialName("created_at") val createdAt: String? = null
)

fun QuoteDto.toDomain(isFavorite: Boolean = false): Quote {
    return Quote(
        id = id,
        text = text,
        author = author,
        category = category,
        isFavorite = isFavorite,
        createdAt = createdAt ?: ""
    )
}
