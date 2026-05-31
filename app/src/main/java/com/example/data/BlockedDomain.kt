package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_domains")
data class BlockedDomain(
    @PrimaryKey val domain: String, // e.g. "betting-site.com"
    val reason: String = "Taqiqlangan sayt",
    val dateAdded: Long = System.currentTimeMillis()
)
