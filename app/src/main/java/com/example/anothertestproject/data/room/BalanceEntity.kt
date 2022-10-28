package com.example.anothertestproject.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "balance", foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("user"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class BalanceEntity(
    @PrimaryKey(autoGenerate = true)
    val balanceId: Long? = null,
    val balance: Double,
    val currency: String,
    @ColumnInfo(index = true)
    val user: Long?
)
