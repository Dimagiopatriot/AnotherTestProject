package com.example.anothertestproject.data.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface LibraryDao {

    @Insert(onConflict = REPLACE)
    fun saveUser(user: UserEntity): Long

    @Insert
    fun saveBalance(vararg balanceEntity: BalanceEntity)

    @Update
    fun updateBalances(vararg userBalances: BalanceEntity)

    @Transaction
    @Query("SELECT * FROM user")
    suspend fun getUsers(): List<UserAndBalance>

    @Transaction
    @Query("SELECT * FROM user WHERE uid = :uid")
    suspend fun getUserById(uid: Long): UserAndBalance
}