package com.example

import android.content.Context
import android.os.Bundle
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.service.BrowserAccessibilityService
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

enum class ShieldTab(val title: String, val icon: ImageVector) {
    DASHBOARD("Meyor", Icons.Default.Home),
    PROTECTION("SMS/Call", Icons.Default.Security),
    MONITORING("Veb-Filtr", Icons.Default.Language),
    APK_SCAN("Scanner", Icons.Default.QrCodeScanner),
    SETTINGS("Sozlamalar", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val shieldViewModel: ShieldViewModel = viewModel()
            val isDarkMode by shieldViewModel.isDarkModeSetting.collectAsState()

            MyApplicationTheme(darkTheme = isDarkMode) {
                MainAppLayout(shieldViewModel = shieldViewModel)
            }
        }
    }
}

@Composable
fun MainAppLayout(shieldViewModel: ShieldViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isOnboardingCompleted by shieldViewModel.isOnboardingCompleted.collectAsState()
    val currentLanguage by shieldViewModel.currentLanguageSetting.collectAsState()
    var systemPermissionsGranted by remember { mutableStateOf(checkCurrentPermissionsSync(context)) }
    var showSplash by remember { mutableStateOf(true) }

    // Synchronize permission checking when returning to the application (ON_RESUME)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkCurrentPermissions(context) { sms, phone, overlay, notification ->
                    systemPermissionsGranted = sms && phone && overlay && notification
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (showSplash) {
        LogoSplashScreen(
            currentLanguage = currentLanguage,
            onLanguageChange = { shieldViewModel.setLanguage(it) },
            onDismissSplash = { showSplash = false }
        )
    } else if (!systemPermissionsGranted && !isOnboardingCompleted) {
        PermissionOnboardingScreen(
            onPermissionsGranted = { shieldViewModel.setOnboardingCompleted(true) }
        )
    } else {
        MainNavigationContainer(shieldViewModel = shieldViewModel)
    }
}

@Composable
fun MainNavigationContainer(shieldViewModel: ShieldViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(ShieldTab.DASHBOARD) }

    // Collect Room DB Flows
    val spamNumbersFlow = shieldViewModel.allSpamNumbers
    val spamNumbers by spamNumbersFlow.collectAsState(initial = emptyList())
    val spamLogsFlow = shieldViewModel.allSpamLogs
    val spamLogs by spamLogsFlow.collectAsState(initial = emptyList())
    val blockedDomainsFlow = shieldViewModel.allBlockedDomains
    val blockedDomains by blockedDomainsFlow.collectAsState(initial = emptyList())
    
    // Settings state
    val adminPin by shieldViewModel.adminPin.collectAsState()
    val spamUrl by shieldViewModel.spamDownloadUrl.collectAsState()
    val isShieldActive by shieldViewModel.isShieldActiveSetting.collectAsState()
    val isWebFilterActive by shieldViewModel.isWebFilterActiveSetting.collectAsState()
    val isDarkMode by shieldViewModel.isDarkModeSetting.collectAsState()

    // Realtime global UI states
    val currentLanguage by shieldViewModel.currentLanguageSetting.collectAsState()
    val isAdminUnlocked by shieldViewModel.isAdminUnlocked.collectAsState()

    // Determine accessibility service status
    var isWebAccessibilityActive by remember { mutableStateOf(false) }

    // Check accessibility service state on screen resume / tab shifts
    LaunchedEffect(selectedTab) {
        isWebAccessibilityActive = isAccessibilityServiceEnabled(context)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // Immersive Glassmorphic Bottom Navigation
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp) // Adjusted height to ensure large, clear icons
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShieldTab.values().forEach { tab ->
                        val active = selectedTab == tab
                        val activeColor = MaterialTheme.colorScheme.primary
                        val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedTab = tab }
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (active) activeColor.copy(alpha = 0.15f) else Color.Transparent)
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    tint = if (active) activeColor else inactiveColor,
                                    modifier = Modifier.size(28.dp) // Large design icons for enhanced visibility
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            val dynamicTitle = when (tab) {
                                ShieldTab.DASHBOARD -> com.example.ui.components.Localization.getString(currentLanguage, "dashboard")
                                ShieldTab.PROTECTION -> com.example.ui.components.Localization.getString(currentLanguage, "protection")
                                ShieldTab.MONITORING -> com.example.ui.components.Localization.getString(currentLanguage, "monitoring")
                                ShieldTab.APK_SCAN -> com.example.ui.components.Localization.getString(currentLanguage, "apk_scan")
                                ShieldTab.SETTINGS -> com.example.ui.components.Localization.getString(currentLanguage, "settings")
                            }
                            Text(
                                text = dynamicTitle,
                                color = if (active) activeColor else inactiveColor,
                                fontSize = 10.5.sp,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = selectedTab, label = "tabs") { tab ->
                when (tab) {
                    ShieldTab.DASHBOARD -> DashboardScreen(
                        currentLanguage = currentLanguage,
                        spamLogFlow = shieldViewModel.recentSpamLogs,
                        spamCount = spamLogs.count { it.actionTaken == "BLOKLANDI" },
                        blockedWebsitesCount = blockedDomains.size,
                        isShieldActive = isShieldActive,
                        onToggleShield = { shieldViewModel.setShieldActive(it) },
                        onNavigateToLogs = { selectedTab = ShieldTab.PROTECTION },
                        onTriggerManualSync = { shieldViewModel.syncSpamDatabase() },
                        isWebFilterActive = isWebAccessibilityActive,
                        onLanguageChange = { shieldViewModel.setLanguage(it) }
                    )

                    ShieldTab.PROTECTION -> ProtectionScreen(
                        currentLanguage = currentLanguage,
                        isAdminUnlocked = isAdminUnlocked,
                        spamNumbersFlow = shieldViewModel.allSpamNumbers,
                        spamLogsFlow = shieldViewModel.allSpamLogs,
                        spamKeywordsFlow = shieldViewModel.spamKeywordsSetting,
                        remoteUrl = spamUrl,
                        onSaveRemoteUrl = { shieldViewModel.saveSpamUrl(it) },
                        onSyncRemote = { shieldViewModel.syncSpamDatabase() },
                        onAddSpamNumber = { phone, label -> shieldViewModel.addSpamNumber(phone, label) },
                        onDeleteSpamNumber = { shieldViewModel.removeSpamNumber(it) },
                        onClearSpamLogs = { shieldViewModel.clearAllSpamLogs() },
                        onImportLocalTxt = { text -> shieldViewModel.importLocalTextContent(text) },
                        onAddSpamKeyword = { wd -> shieldViewModel.addSpamKeyword(wd) },
                        onRemoveSpamKeyword = { wd -> shieldViewModel.removeSpamKeyword(wd) }
                    )

                    ShieldTab.MONITORING -> MonitoringScreen(
                        currentLanguage = currentLanguage,
                        isAdminUnlocked = isAdminUnlocked,
                        blockedDomainsFlow = shieldViewModel.allBlockedDomains,
                        isAccessibilityServiceActive = isWebAccessibilityActive,
                        onAddDomain = { desc, reason -> shieldViewModel.addBlockedDomain(desc, reason) },
                        onDeleteDomain = { shieldViewModel.removeBlockedDomain(it) }
                    )

                    ShieldTab.APK_SCAN -> ApkScanScreen(
                        currentLanguage = currentLanguage,
                        scanResultsFlow = shieldViewModel.allScanResults,
                        onSaveScanResult = { s -> shieldViewModel.insertScanResult(s) },
                        onClearScanResults = { shieldViewModel.clearAllScanResults() }
                    )

                    ShieldTab.SETTINGS -> SettingsScreen(
                        currentLanguage = currentLanguage,
                        onLanguageChange = { shieldViewModel.setLanguage(it) },
                        isAdminUnlocked = isAdminUnlocked,
                        onAdminUnlockedChange = { shieldViewModel.setAdminUnlocked(it) },
                        currentPin = adminPin,
                        onSavePin = { shieldViewModel.savePin(it) },
                        onClearDatabase = { shieldViewModel.clearAndResetAllDatabase() },
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { shieldViewModel.setDarkMode(it) }
                    )
                }
            }
        }
    }
}

fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val serviceCanonicalName = BrowserAccessibilityService::class.java.canonicalName ?: return false
    val expectedName = context.packageName + "/" + serviceCanonicalName
    val settingValue = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false
    return settingValue.contains(expectedName)
}

fun checkCurrentPermissionsSync(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val hasSms = context.checkSelfPermission(android.Manifest.permission.RECEIVE_SMS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val hasPhone = context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val hasCallLog = context.checkSelfPermission(android.Manifest.permission.READ_CALL_LOG) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val hasOverlay = Settings.canDrawOverlays(context)
        val hasNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        return hasSms && hasPhone && hasCallLog && hasOverlay && hasNotification
    }
    return true
}
