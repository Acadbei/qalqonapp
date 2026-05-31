package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSetting(
    @PrimaryKey val key: String,
    val value: String
) {
    companion object {
        const val KEY_PIN_CODE = "admin_pin_code"
        const val KEY_SPAM_URL = "spam_download_url"
        const val KEY_SHIELD_ACTIVE = "shield_active_state"
        const val KEY_WEB_FILTER_ACTIVE = "web_filter_state"
        const val KEY_DARK_MODE = "dark_mode_state"
        const val KEY_ONBOARDED = "onboarded_state"
    }
}
