package com.example.quotevault.domain.model

data class Collection(
val id: String,
val userId: String,
val name: String,
val description: String?,
val createdAt: String,
val quoteCount: Int = 0
)


data class CollectionWithQuotes(
val collection: Collection,
val quotes: List<Quote>
)