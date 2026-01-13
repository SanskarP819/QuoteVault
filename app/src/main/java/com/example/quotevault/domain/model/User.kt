package com.example.quotevault.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String? = null,
    val avatarUrl: String? = null
)