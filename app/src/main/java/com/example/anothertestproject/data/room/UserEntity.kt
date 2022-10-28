package com.example.anothertestproject.data.room

import androidx.room.*

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val uid: Long? = null
)
