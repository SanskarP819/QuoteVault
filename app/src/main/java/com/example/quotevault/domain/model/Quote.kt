package com.example.quotevault.domain.model

data class Quote(
    val id: String,
    val text: String,
    val author: String,
    val category: String,
    val isFavorite: Boolean = false,
    val createdAt: String = ""
)