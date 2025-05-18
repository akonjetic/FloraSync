package com.example.florasync.network

data class PagedResponse<T>(
    val items: List<T>,
    val currentPage: Int,
    val totalPages: Int
)
