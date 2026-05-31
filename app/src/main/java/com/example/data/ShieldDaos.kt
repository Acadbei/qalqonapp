package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SpamNumberDao {
    @Query("SELECT * FROM spam_numbers ORDER BY dateAdded DESC")
    fun getAllSpamNumbersFlow(): Flow<List<SpamNumber>>

    @Query("SELECT * FROM spam_numbers")
    suspend fun getAllSpamNumbers(): List<SpamNumber>

    @Query("SELECT * FROM spam_numbers WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getSpamNumber(phone: String): SpamNumber?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpamNumbers(numbers: List<SpamNumber>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpamNumber(number: SpamNumber)

    @Delete
    suspend fun deleteSpamNumber(number: SpamNumber)

    @Query("DELETE FROM spam_numbers")
    suspend fun deleteAll()
}

@Dao
interface SpamLogDao {
    @Query("SELECT * FROM spam_logs ORDER BY timestamp DESC")
    fun getAllSpamLogsFlow(): Flow<List<SpamLog>>

    @Query("SELECT * FROM spam_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentSpamLogsFlow(): Flow<List<SpamLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpamLog(log: SpamLog)

    @Query("DELETE FROM spam_logs")
    suspend fun deleteAll()
}

@Dao
interface BlockedDomainDao {
    @Query("SELECT * FROM blocked_domains ORDER BY dateAdded DESC")
    fun getAllBlockedDomainsFlow(): Flow<List<BlockedDomain>>

    @Query("SELECT * FROM blocked_domains")
    suspend fun getAllBlockedDomains(): List<BlockedDomain>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedDomains(domains: List<BlockedDomain>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedDomain(domain: BlockedDomain)

    @Delete
    suspend fun deleteBlockedDomain(domain: BlockedDomain)

    @Query("DELETE FROM blocked_domains")
    suspend fun deleteAll()
}

@Dao
interface ApkScanResultDao {
    @Query("SELECT * FROM apk_scan_results ORDER BY scanTimestamp DESC")
    fun getAllScanResultsFlow(): Flow<List<ApkScanResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanResults(results: List<ApkScanResult>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanResult(result: ApkScanResult)

    @Query("DELETE FROM apk_scan_results")
    suspend fun deleteAll()
}

@Dao
interface AppSettingDao {
    @Query("SELECT * FROM app_settings WHERE `key` = :key LIMIT 1")
    suspend fun getSetting(key: String): AppSetting?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: AppSetting)
}
