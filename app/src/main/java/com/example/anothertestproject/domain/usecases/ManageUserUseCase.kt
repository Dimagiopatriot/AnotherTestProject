package com.example.anothertestproject.domain.usecases

import com.example.anothertestproject.data.Rate
import com.example.anothertestproject.domain.replace
import com.example.anothertestproject.repositories.user.BalanceDto
import com.example.anothertestproject.repositories.user.UserDto
import com.example.anothertestproject.repositories.user.UserRepository

class ManageUserUseCase(
    private val userRepository: UserRepository
) {

    suspend fun getUser() = userRepository.getUser()

    suspend fun updateUserBalances(vararg balanceDto: BalanceDto, userId: Long) =
        userRepository.updateUserBalances(*balanceDto, userId = userId)

    suspend fun initiateUserSave(rates: List<Rate>, initialBalance: BalanceDto) {
        val userBalances = rates
            .map { BalanceDto(currency = it.currency, balance = 0.0) }
            .replace(initialBalance) { it.currency == initialBalance.currency }
        val userDto = UserDto(balances = userBalances)
        userRepository.saveUser(userDto)
    }
}