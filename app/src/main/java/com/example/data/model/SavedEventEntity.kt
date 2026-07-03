package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_events")
data class SavedEventEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val eventId: String,
    val timestamp: Long = System.currentTimeMillis()
)
