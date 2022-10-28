package com.example.anothertestproject.repositories.user

data class BalanceDto(
    val balanceId: Long? = null,
    val balance: Double,
    val currency: String,
)