package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.net.URL

class ShieldRepository(private val db: ShieldDatabase) {

    val allSpamNumbersFlow: Flow<List<SpamNumber>> = db.spamNumberDao().getAllSpamNumbersFlow()
    val allSpamLogsFlow: Flow<List<SpamLog>> = db.spamLogDao().getAllSpamLogsFlow()
    val recentSpamLogsFlow: Flow<List<SpamLog>> = db.spamLogDao().getRecentSpamLogsFlow()
    val allBlockedDomainsFlow: Flow<List<BlockedDomain>> = db.blockedDomainDao().getAllBlockedDomainsFlow()
    val allScanResultsFlow: Flow<List<ApkScanResult>> = db.apkScanResultDao().getAllScanResultsFlow()

    suspend fun getSpamNumber(phone: String): SpamNumber? = withContext(Dispatchers.IO) {
        val cleanPhone = cleanPhoneNumber(phone)
        val list = db.spamNumberDao().getAllSpamNumbers()
        if (cleanPhone.length >= 9) {
            val phoneSuffix = cleanPhone.takeLast(9)
            list.find {
                val dbCleaned = cleanPhoneNumber(it.phoneNumber)
                dbCleaned.length >= 9 && dbCleaned.takeLast(9) == phoneSuffix
            }
        } else {
            list.find { cleanPhoneNumber(it.phoneNumber) == cleanPhone }
        }
    }

    suspend fun isDomainBlocked(url: String): BlockedDomain? = withContext(Dispatchers.IO) {
        val host = getHostName(url).lowercase()
        val list = db.blockedDomainDao().getAllBlockedDomains()
        list.find { blocked ->
            val blockedDomain = blocked.domain.lowercase().trim()
            host.endsWith(blockedDomain) || blockedDomain.endsWith(host)
        }
    }

    suspend fun insertSpamNumber(number: SpamNumber) = withContext(Dispatchers.IO) {
        db.spamNumberDao().insertSpamNumber(number)
    }

    suspend fun deleteSpamNumber(number: SpamNumber) = withContext(Dispatchers.IO) {
        db.spamNumberDao().deleteSpamNumber(number)
    }

    suspend fun insertBlockedDomain(domain: BlockedDomain) = withContext(Dispatchers.IO) {
        db.blockedDomainDao().insertBlockedDomain(domain)
    }

    suspend fun deleteBlockedDomain(domain: BlockedDomain) = withContext(Dispatchers.IO) {
        db.blockedDomainDao().deleteBlockedDomain(domain)
    }

    suspend fun insertSpamLog(log: SpamLog) = withContext(Dispatchers.IO) {
        db.spamLogDao().insertSpamLog(log)
    }

    suspend fun insertScanResults(results: List<ApkScanResult>) = withContext(Dispatchers.IO) {
        db.apkScanResultDao().insertScanResults(results)
    }

    suspend fun insertScanResult(result: ApkScanResult) = withContext(Dispatchers.IO) {
        db.apkScanResultDao().insertScanResult(result)
    }

    suspend fun clearAllScanResults() = withContext(Dispatchers.IO) {
        db.apkScanResultDao().deleteAll()
    }

    // Settings
    suspend fun saveSetting(key: String, value: String) = withContext(Dispatchers.IO) {
        db.appSettingDao().saveSetting(AppSetting(key, value))
    }

    suspend fun getSetting(key: String, defaultValue: String): String = withContext(Dispatchers.IO) {
        db.appSettingDao().getSetting(key)?.value ?: defaultValue
    }

    // Prepopulate DB
    suspend fun prepopulateDefaults() = withContext(Dispatchers.IO) {
        val listNum = db.spamNumberDao().getAllSpamNumbers()
        if (listNum.isEmpty()) {
            val defaults = listOf(
                SpamNumber("+998901234567", "Spam / Yolg'on Yutuqlar"),
                SpamNumber("+998335557788", "Kibershantaj / Firibgar"),
                SpamNumber("+998991112233", "Spam / Bloklangan Bot"),
                SpamNumber("+998931110022", "Reklama Qo'ng'irog'i"),
                SpamNumber("+998500013991", "Spam / Bloklangan Raqam")
            )
            db.spamNumberDao().insertSpamNumbers(defaults)
        } else {
            val hasTarget = listNum.any { it.phoneNumber == "+998500013991" }
            if (!hasTarget) {
                db.spamNumberDao().insertSpamNumber(SpamNumber("+998500013991", "Spam / Bloklangan Raqam"))
            }
        }

        val listDom = db.blockedDomainDao().getAllBlockedDomains()
        if (listDom.isEmpty()) {
            val defaultsDom = listOf(
                BlockedDomain("1xbet.com", "Tikish va qimor o'yinlari"),
                BlockedDomain("mostbet.com", "Tikish va qimor o'yinlari"),
                BlockedDomain("melbet.com", "Tikish va qimor o'yinlari"),
                BlockedDomain("pin-up.casino", "Onlayn kazino"),
                BlockedDomain("tiktok.com", "Sog'lom hayot / Parental-Control"),
                BlockedDomain("pubg.co", "Taqiqlangan o'yinlar sayti")
            )
            db.blockedDomainDao().insertBlockedDomains(defaultsDom)
        }

        // Default PIN
        val pin = db.appSettingDao().getSetting(AppSetting.KEY_PIN_CODE)
        if (pin == null) {
            db.appSettingDao().saveSetting(AppSetting(AppSetting.KEY_PIN_CODE, "945652"))
        }

        // Default URL
        val spamUrl = db.appSettingDao().getSetting(AppSetting.KEY_SPAM_URL)
        if (spamUrl == null) {
            db.appSettingDao().saveSetting(AppSetting(AppSetting.KEY_SPAM_URL, "https://raw.githubusercontent.com/spam-list/main/spam.txt"))
        }
    }

    // Import from TXT file string content (CPanel / Local)
    suspend fun importFromTxtContent(text: String): Int = withContext(Dispatchers.IO) {
        var count = 0
        val numbers = mutableListOf<SpamNumber>()
        val domains = mutableListOf<BlockedDomain>()

        text.lineSequence().forEach { line ->
            val cleanLine = line.trim()
            if (cleanLine.isEmpty() || cleanLine.startsWith("#")) return@forEach

            if (cleanLine.startsWith("nomer:", ignoreCase = true)) {
                val value = cleanLine.substring(6).trim().split(",")
                val phone = value.getOrNull(0)?.trim()
                val label = value.getOrNull(1)?.trim() ?: "Tizim Spami"
                if (!phone.isNullOrEmpty()) {
                    numbers.add(SpamNumber(phone, label))
                    count++
                }
            } else if (cleanLine.startsWith("sayt:", ignoreCase = true)) {
                val value = cleanLine.substring(5).trim().split(",")
                val host = value.getOrNull(0)?.trim()
                val reason = value.getOrNull(1)?.trim() ?: "Administrator Taqiqladi"
                if (!host.isNullOrEmpty()) {
                    domains.add(BlockedDomain(host, reason))
                    count++
                }
            } else {
                // Autodetect
                if (cleanLine.contains(".") && !cleanLine.startsWith("+") && !cleanLine.contains(Regex("[0-9]{5,}"))) {
                    domains.add(BlockedDomain(cleanLine, "Spam domen"))
                    count++
                } else if (cleanLine.startsWith("+") || cleanLine.matches(Regex("[0-9+() -]+"))) {
                    numbers.add(SpamNumber(cleanLine, "Import qilingan spam"))
                    count++
                }
            }
        }

        if (numbers.isNotEmpty()) {
            db.spamNumberDao().insertSpamNumbers(numbers)
        }
        if (domains.isNotEmpty()) {
            db.blockedDomainDao().insertBlockedDomains(domains)
        }
        count
    }

    // Sync from remote CPanel txt URL
    suspend fun syncFromRemoteUrl(urlStr: String): Int = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(urlStr).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@use 0
                val bodyStr = response.body?.string() ?: return@use 0
                return@use importFromTxtContent(bodyStr)
            }
        } catch (e: Exception) {
            Log.e("ShieldRepository", "Synch failed: ${e.message}")
            0
        }
    }

    private fun cleanPhoneNumber(phone: String): String {
        return phone.replace(Regex("[^+0-9]"), "")
    }

    private fun getHostName(url: String): String {
        var host = url.replace(Regex("^(https?://)?(www\\.)?"), "")
        val slashIndex = host.indexOf('/')
        if (slashIndex != -1) {
            host = host.substring(0, slashIndex)
        }
        val colonIndex = host.indexOf(':')
        if (colonIndex != -1) {
            host = host.substring(0, colonIndex)
        }
        return host.trim()
    }
}
