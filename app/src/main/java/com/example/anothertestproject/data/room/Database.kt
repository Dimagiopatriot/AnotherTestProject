package com.example.anothertestproject.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BalanceEntity::class, UserEntity::class], version = 1, exportSchema = false)
abstract class Database: RoomDatabase() {
    abstract fun getLibraryDao() : LibraryDao
}