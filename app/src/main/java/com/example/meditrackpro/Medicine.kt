package com.example.meditrackpro

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val dose: String,
    val times: String,        // e.g. "08:00, 20:00"
    val refillDays: Int,
    val color: String         // e.g. "accent", "blue", "purple", "gold"
)