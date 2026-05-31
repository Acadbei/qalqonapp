package com.example.ui.screens

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ApkScanResult
import com.example.ui.components.CyberNeonDivider
import com.example.ui.components.GlassmorphicCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ApkScanScreen(
    scanResultsFlow: Flow<List<ApkScanResult>>,
    onSaveScanResult: (ApkScanResult) -> Unit,
    onClearScanResults: () -> Unit
) {
    val context = LocalContext.current
    val historicResults by scanResultsFlow.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    var isScanning by remember { mutableStateOf(false) }
    var currentScannedApp by remember { mutableStateOf("") }
    var scanProgress by remember { mutableStateOf(0f) }
    
    var localScanResults by remember { mutableStateOf<List<ApkScanResult>>(emptyList()) }
    var showReport by remember { mutableStateOf(false) }

    // Cyber radar spinning animation
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    fun startApkScan() {
        coroutineScope.launch {
            isScanning = true
            showReport = false
            scanProgress = 0f
            localScanResults = emptyList()

            val pm = context.packageManager
            val packages = withContext(Dispatchers.IO) {
                try {
                    pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
                } catch (e: Exception) {
                    emptyList<PackageInfo>()
                }
            }

            // Filter non-system apps where possible, or limit to 50 apps for speed
            val appsToScan = packages.filter {
                val appInfo = it.applicationInfo
                if (appInfo != null) {
                    val isSystem = (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                    !isSystem
                } else {
                    false
                }
            }.take(50).ifEmpty { packages.take(30) }

            val total = appsToScan.size.toFloat()
            if (total == 0f) {
                isScanning = false
                showReport = true
                return@launch
            }
            
            appsToScan.forEachIndexed { index, packageInfo ->
                val appInfo = packageInfo.applicationInfo
                val appLabel = appInfo?.loadLabel(pm)?.toString() ?: packageInfo.packageName ?: "Noma'lum ilova"
                currentScannedApp = appLabel
                scanProgress = (index + 1) / total
                
                // Permission hazard check
                val permissions = packageInfo.requestedPermissions ?: emptyArray()
                val suspectPermissions = mutableListOf<String>()
                
                permissions.forEach { perm ->
                    if (perm.contains("RECEIVE_SMS") || perm.contains("READ_SMS") || perm.contains("SEND_SMS")) {
                        suspectPermissions.add("SMS")
                    }
                    if (perm.contains("SYSTEM_ALERT_WINDOW")) {
                        suspectPermissions.add("Overlay")
                    }
                    if (perm.contains("BIND_ACCESSIBILITY_SERVICE")) {
                        suspectPermissions.add("Accessibility")
                    }
                    if (perm.contains("READ_PHONE_STATE") || perm.contains("CALL_PHONE")) {
                        suspectPermissions.add("Phone Call")
                    }
                }

                val risk = when {
                    suspectPermissions.contains("Accessibility") || (suspectPermissions.contains("SMS") && suspectPermissions.contains("Overlay")) -> "YUQORI XAVFLI"
                    suspectPermissions.isNotEmpty() -> "SHUBHALI"
                    else -> "XAVFSIZ"
                }

                val details = if (suspectPermissions.isNotEmpty()) {
                    "Seziluvchan ruxsatlar: " + suspectPermissions.joinToString(", ")
                } else {
                    "Xavfli ruxsatlar aniqlanmadi"
                }

                val scanItem = ApkScanResult(
                    packageName = packageInfo.packageName,
                    appName = appLabel,
                    riskLevel = risk,
                    riskDetails = details
                )

                // Slow down a bit to let the user see the scanning animation progression
                delay(100)

                localScanResults = localScanResults + scanItem
                // Save report directly to Room
                onSaveScanResult(scanItem)
            }

            isScanning = false
            currentScannedApp = ""
            showReport = true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Section Header
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = "QALQON APK SCANNER",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Yashirin malware, keylogger va josus ruxsatlarni aniqlash",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }

        if (!isScanning && !showReport) {
            // Idle Screen
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            shape = CircleShape,
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Shield Guard",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Ilovalarni tahlil qilish",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = "Barcha o'rnatilgan paketlarni tahlil qiling. Tizim maxfiylik ruxsatlarini (SMS o'qish, Oyna ustidan chizish, Shaxsiy xizmatlar) skanerlab, dushmanga qarshi xisobot shakllantiradi.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )

                    Button(
                        onClick = { startApkScan() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("start_scan_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Scan")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("YANGI SKANERLASH", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else if (isScanning) {
            // Scanning Mode with rotating Radar
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Box(
                        modifier = Modifier.size(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Ambient radar dials
                        Box(modifier = Modifier.size(180.dp).border(0.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), CircleShape))
                        Box(modifier = Modifier.size(140.dp).border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f), CircleShape))
                        Box(modifier = Modifier.size(100.dp).border(1.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f), CircleShape))

                        // Spinning Sweep line
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .rotate(rotation),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(90.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(MaterialTheme.colorScheme.secondary, Color.Transparent)
                                        )
                                    )
                             )
                        }

                        // Code Scanner info value
                        Text(
                            text = "${(scanProgress * 100).toInt()}%",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp
                        )
                    }

                    Text(
                        text = "SKANERLANMOQDA...",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = currentScannedApp.ifEmpty { "Tahlil qilinmoqda..." },
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    )

                    LinearProgressIndicator(
                        progress = { scanProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }
            }
        } else {
            // Scan Complete Report List
            val highs = localScanResults.count { it.riskLevel == "YUQORI XAVFLI" }
            val suspects = localScanResults.count { it.riskLevel == "SHUBHALI" }
            val safes = localScanResults.count { it.riskLevel == "XAVFSIZ" }

            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "SKANERLASH YAKUNLANDI",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                        
                        Text(
                            text = "Jami ${localScanResults.size} ta paket tekshirildi",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        )

                        CyberNeonDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))

                        // Stats Grid indicators
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "$highs", color = Color(0xFFFF1744), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(text = "O'ta xavfli", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "$suspects", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(text = "Shubhali", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "$safes", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(text = "Xavfsiz", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                        }

                        Button(
                            onClick = { startApkScan() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Qayta skanerlash", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "TAHLIL NATIJALARI",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
            }

            items(localScanResults.sortedBy { 
                when (it.riskLevel) {
                    "YUQORI XAVFLI" -> 1
                    "SHUBHALI" -> 2
                    else -> 3
                }
            }) { app ->
                val badgeColor = when (app.riskLevel) {
                    "YUQORI XAVFLI" -> Color(0xFFFF1744)
                    "SHUBHALI" -> Color(0xFFFFC107)
                    else -> Color(0xFF00E676)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            color = badgeColor.copy(alpha = 0.12f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, badgeColor)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (app.riskLevel == "XAVFSIZ") Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = "Threat",
                                    tint = badgeColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Column {
                            Text(text = app.appName, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = app.riskDetails, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f), fontSize = 11.sp, lineHeight = 14.sp)
                        }
                    }

                    Surface(
                        color = badgeColor.copy(alpha = 0.15f),
                        border = BorderStroke(0.5.dp, badgeColor),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = app.riskLevel,
                            color = badgeColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 8.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
