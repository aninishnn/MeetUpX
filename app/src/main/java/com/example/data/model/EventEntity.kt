package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val date: String,
    val time: String,
    val location: String,
    val imageName: String,
    val creatorId: String,
    val isCustom: Boolean = false
)
