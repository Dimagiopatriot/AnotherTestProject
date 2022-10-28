package com.example.anothertestproject.repositories.user

import com.example.anothertestproject.data.room.BalanceEntity
import com.example.anothertestproject.data.room.LibraryDao
import com.example.anothertestproject.data.room.UserEntity

class RoomUserRepositoryImpl(
    private val libraryDao: LibraryDao
) : UserRepository {
    override suspend fun saveUser(userDto: UserDto) {
        val userEntity = UserEntity()
        val userId = libraryDao.saveUser(userEntity)
        if (userDto.balances.isNotEmpty()) {
            val balanceEntities = userDto.balances.map {
                BalanceEntity(balance = it.balance, currency = it.currency, user = userId)
            }
            libraryDao.saveBalance(*balanceEntities.toTypedArray())
        }
    }

    override suspend fun getUser(userId: Long?): UserDto? {
        return userId?.let {
            val userAndBalance = libraryDao.getUserById(it)
            val userDto = UserDto(
                uid = userAndBalance.user.uid,
                balances = userAndBalance.balances.map { balanceEntity ->
                    BalanceDto(
                        balanceEntity.balanceId,
                        balanceEntity.balance,
                        balanceEntity.currency
                    )
                }
            )
            userDto
        } ?: run {
            val userAndBalance = libraryDao.getUsers()
            if (userAndBalance.isNotEmpty()) {
                val userDto = UserDto(
                    uid = userAndBalance[0].user.uid,
                    balances = userAndBalance[0].balances.map { balanceEntity ->
                        BalanceDto(
                            balanceEntity.balanceId,
                            balanceEntity.balance,
                            balanceEntity.currency
                        )
                    }
                )
                userDto
            } else {
                null
            }
        }
    }

    override suspend fun updateUserBalances(vararg balanceDto: BalanceDto, userId: Long?) {
        val balanceEntities = balanceDto.map {
            BalanceEntity(it.balanceId, it.balance, it.currency, userId)
        }
        libraryDao.updateBalances(*balanceEntities.toTypedArray())
    }

}