package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apk_scan_results")
data class ApkScanResult(
    @PrimaryKey val packageName: String,
    val appName: String,
    val riskLevel: String, // "XAVFSIZ", "SHUBHALI", "YUQORI XAVFLI"
    val riskDetails: String, // list of suspicious permissions detected
    val scanTimestamp: Long = System.currentTimeMillis()
)
