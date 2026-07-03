package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "joined_events")
data class JoinedEventEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val eventId: String,
    val qrCodeReference: String,
    val timestamp: Long = System.currentTimeMillis()
)
