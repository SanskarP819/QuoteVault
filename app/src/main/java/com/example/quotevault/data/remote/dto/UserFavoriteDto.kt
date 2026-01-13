package com.example.quotevault.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserFavoriteDto(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("quote_id") val quoteId: String,
    @SerialName("created_at") val createdAt: String? = null
)