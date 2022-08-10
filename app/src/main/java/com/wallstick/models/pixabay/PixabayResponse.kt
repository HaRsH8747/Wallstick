package com.wallstick.models.pixabay

data class PixabayResponse(
    val hits: List<Hit>,
    val total: Int,
    val totalHits: Int
)