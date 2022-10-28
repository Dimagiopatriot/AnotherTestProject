package com.example.anothertestproject.repositories.user

interface UserRepository {

    suspend fun saveUser(userDto: UserDto)

    suspend fun getUser(userId: Long? = null): UserDto?

    suspend fun updateUserBalances(vararg balanceDto: BalanceDto, userId: Long?)
}