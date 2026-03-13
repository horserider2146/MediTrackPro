package com.example.meditrackpro

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_records")
data class HealthRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String,       // "Lab Result", "Prescription", "Doctor Visit", "Vaccination"
    val date: String,
    val status: String,     // "Normal", "Active", "Routine", "Done", etc.
    val notes: String = ""
)