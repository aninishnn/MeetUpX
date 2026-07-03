package com.example.data.model

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val date: String,
    val time: String,
    val location: String,
    val imageName: String,
    val creatorId: String = "admin_meetupx",
    val isCustom: Boolean = false
)
