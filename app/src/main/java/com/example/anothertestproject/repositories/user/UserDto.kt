package com.example.anothertestproject.repositories.user

data class UserDto(
    val uid: Long? = null,
    var balances: List<BalanceDto>
)