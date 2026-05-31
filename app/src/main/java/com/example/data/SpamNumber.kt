package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spam_numbers")
data class SpamNumber(
    @PrimaryKey val phoneNumber: String,
    val label: String,
    val dateAdded: Long = System.currentTimeMillis()
)
