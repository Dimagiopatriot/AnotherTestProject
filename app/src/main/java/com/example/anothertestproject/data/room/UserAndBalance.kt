package com.example.anothertestproject.data.room

import androidx.room.Embedded
import androidx.room.Relation

data class UserAndBalance(
    @Embedded
    val user: UserEntity,
    @Relation(
        parentColumn = "uid",
        entityColumn = "user"
    )
    val balances: List<BalanceEntity>
)
