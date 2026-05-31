package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SpamNumber::class,
        SpamLog::class,
        BlockedDomain::class,
        ApkScanResult::class,
        AppSetting::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ShieldDatabase : RoomDatabase() {
    abstract fun spamNumberDao(): SpamNumberDao
    abstract fun spamLogDao(): SpamLogDao
    abstract fun blockedDomainDao(): BlockedDomainDao
    abstract fun apkScanResultDao(): ApkScanResultDao
    abstract fun appSettingDao(): AppSettingDao

    companion object {
        @Volatile
        private var INSTANCE: ShieldDatabase? = null

        fun getDatabase(context: Context): ShieldDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShieldDatabase::class.java,
                    "shield_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
