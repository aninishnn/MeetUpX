package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val bio: String,
    val university: String,
    val profilePictureUrl: String = ""
)
