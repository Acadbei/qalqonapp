package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spam_logs")
data class SpamLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String,
    val message: String, // Or "KIRUVCHI QO'NG'IROQ" for calls
    val type: String, // "SMS" yoki "QO'NG'IROQ"
    val timestamp: Long = System.currentTimeMillis(),
    val actionTaken: String // "BLOKLANDI" hamda "RUXSAT BERILDI"
)
