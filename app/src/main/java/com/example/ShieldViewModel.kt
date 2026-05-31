package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShieldViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ShieldDatabase.getDatabase(application)
    private val repository = ShieldRepository(db)

    // Flow listings from database
    val allSpamNumbers = repository.allSpamNumbersFlow
    val allSpamLogs = repository.allSpamLogsFlow
    val recentSpamLogs = repository.recentSpamLogsFlow
    val allBlockedDomains = repository.allBlockedDomainsFlow
    val allScanResults = repository.allScanResultsFlow

    // Settings States
    val adminPin = MutableStateFlow("945652")
    val spamDownloadUrl = MutableStateFlow("https://raw.githubusercontent.com/spam-list/main/spam.txt")
    val isShieldActiveSetting = MutableStateFlow(true)
    val isWebFilterActiveSetting = MutableStateFlow(true)
    val isDarkModeSetting = MutableStateFlow(false)
    val isOnboardingCompleted = MutableStateFlow(true)
    val spamKeywordsSetting = MutableStateFlow<List<String>>(emptyList())
    
    // Shared Global Language and Admin Lock State
    val currentLanguageSetting = MutableStateFlow(com.example.ui.components.AppLanguage.UZ_LATIN)
    val isAdminUnlocked = MutableStateFlow(false)

    init {
        // Init database prepopulate values and read current settings
        viewModelScope.launch {
            repository.prepopulateDefaults()
            loadSettings()
        }
    }

    private suspend fun loadSettings() {
        adminPin.value = repository.getSetting(AppSetting.KEY_PIN_CODE, "945652")
        spamDownloadUrl.value = repository.getSetting(AppSetting.KEY_SPAM_URL, "https://raw.githubusercontent.com/spam-list/main/spam.txt")
        isShieldActiveSetting.value = repository.getSetting(AppSetting.KEY_SHIELD_ACTIVE, "true").toBoolean()
        isWebFilterActiveSetting.value = repository.getSetting(AppSetting.KEY_WEB_FILTER_ACTIVE, "true").toBoolean()
        isDarkModeSetting.value = repository.getSetting(AppSetting.KEY_DARK_MODE, "false").toBoolean()
        isOnboardingCompleted.value = repository.getSetting(AppSetting.KEY_ONBOARDED, "false").toBoolean()
        val savedKeywords = repository.getSetting("spam_keywords", "1xbet,mostbet,melbet,yutuq,yutdingiz,yutib oling,million so'm,kredit,foizsiz,stavka,kazino,vaucher,promokod,sovga,shoshiling")
        spamKeywordsSetting.value = savedKeywords.split(",").map { it.trim().lowercase() }.filter { it.isNotEmpty() }
        
        val savedLang = repository.getSetting("app_language", com.example.ui.components.AppLanguage.UZ_LATIN.name)
        currentLanguageSetting.value = com.example.ui.components.AppLanguage.values().find { it.name == savedLang } ?: com.example.ui.components.AppLanguage.UZ_LATIN
    }

    fun setLanguage(lang: com.example.ui.components.AppLanguage) {
        viewModelScope.launch {
            repository.saveSetting("app_language", lang.name)
            currentLanguageSetting.value = lang
        }
    }

    fun setAdminUnlocked(unlocked: Boolean) {
        isAdminUnlocked.value = unlocked
    }

    fun setDarkMode(darkMode: Boolean) {
        viewModelScope.launch {
            repository.saveSetting(AppSetting.KEY_DARK_MODE, darkMode.toString())
            isDarkModeSetting.value = darkMode
        }
    }

    fun savePin(newPin: String) {
        viewModelScope.launch {
            repository.saveSetting(AppSetting.KEY_PIN_CODE, newPin)
            adminPin.value = newPin
        }
    }

    fun saveSpamUrl(newUrl: String) {
        viewModelScope.launch {
            repository.saveSetting(AppSetting.KEY_SPAM_URL, newUrl)
            spamDownloadUrl.value = newUrl
        }
    }

    fun setShieldActive(active: Boolean) {
        viewModelScope.launch {
            repository.saveSetting(AppSetting.KEY_SHIELD_ACTIVE, active.toString())
            isShieldActiveSetting.value = active
        }
    }

    fun addSpamNumber(phone: String, label: String) {
        viewModelScope.launch {
            repository.insertSpamNumber(SpamNumber(phone, label))
        }
    }

    fun removeSpamNumber(number: SpamNumber) {
        viewModelScope.launch {
            repository.deleteSpamNumber(number)
        }
    }

    fun addBlockedDomain(domain: String, reason: String) {
        viewModelScope.launch {
            repository.insertBlockedDomain(BlockedDomain(domain, reason))
        }
    }

    fun removeBlockedDomain(domain: BlockedDomain) {
        viewModelScope.launch {
            repository.deleteBlockedDomain(domain)
        }
    }

    fun insertScanResult(result: ApkScanResult) {
        viewModelScope.launch {
            repository.insertScanResult(result)
        }
    }

    fun clearAllScanResults() {
        viewModelScope.launch {
            repository.clearAllScanResults()
        }
    }

    fun clearAllSpamLogs() {
        viewModelScope.launch {
            db.spamLogDao().deleteAll()
        }
    }

    fun clearAndResetAllDatabase() {
        viewModelScope.launch {
            db.clearAllTables()
            repository.prepopulateDefaults()
            loadSettings()
            isOnboardingCompleted.value = false
        }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        viewModelScope.launch {
            repository.saveSetting(AppSetting.KEY_ONBOARDED, completed.toString())
            isOnboardingCompleted.value = completed
        }
    }

    // Remote sync
    fun syncSpamDatabase() {
        viewModelScope.launch {
            repository.syncFromRemoteUrl(spamDownloadUrl.value)
        }
    }

    // Text import
    fun importLocalTextContent(text: String) {
        viewModelScope.launch {
            repository.importFromTxtContent(text)
        }
    }

    fun addSpamKeyword(keyword: String) {
        viewModelScope.launch {
            val list = spamKeywordsSetting.value.toMutableList()
            val clean = keyword.trim().lowercase()
            if (clean.isNotEmpty() && !list.contains(clean)) {
                list.add(clean)
                repository.saveSetting("spam_keywords", list.joinToString(","))
                spamKeywordsSetting.value = list
            }
        }
    }

    fun removeSpamKeyword(keyword: String) {
        viewModelScope.launch {
            val list = spamKeywordsSetting.value.toMutableList()
            if (list.remove(keyword)) {
                repository.saveSetting("spam_keywords", list.joinToString(","))
                spamKeywordsSetting.value = list
            }
        }
    }
}
