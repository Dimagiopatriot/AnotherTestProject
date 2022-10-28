package com.example.anothertestproject.repositories.cyrrency

data class Currency(
    val base: String,
    val rates: Map<String, Double>
)