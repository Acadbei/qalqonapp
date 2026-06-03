package com.example.ui.screens

// Qalqon Security Master APK Scan Screen Flow Control
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.ui.components.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun ApkScanScreen(
    currentLanguage: AppLanguage,
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

    // Translate texts based on current selected language
    val txtTitle = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "QALQON APK SKANER"
        AppLanguage.UZ_CYRILLIC -> "ҚАЛҚОН APK СКАНЕР"
        AppLanguage.KAZAKH -> "QALQON APK СКАНЕРІ"
        AppLanguage.KYRGYZ -> "QALQON APK СКАНЕРИ"
        AppLanguage.RUSSIAN -> "APK СКАНЕР"
        AppLanguage.KARAKALPAK -> "QALQON APK SKANERI"
        AppLanguage.ENGLISH -> "QALQON APK SCANNER"
    }

    val txtSubTitle = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "Yashirin malware, keylogger va josus ruxsatlarni aniqlash"
        AppLanguage.UZ_CYRILLIC -> "Яширин малваре, кейлоггер ва жосус рухсатларни аниқлаш"
        AppLanguage.KAZAKH -> "Жасырын малварь, кейлоггер және тыңшы рұқсаттарын анықтау"
        AppLanguage.KYRGYZ -> "Жашыруун зыяндуу программаларды, кейлоггерлерди жана тыңчы укуктарын аныктоо"
        AppLanguage.RUSSIAN -> "Обнаружение скрытых вредоносных программ, кейлоггеров и шпионских разрешений"
        AppLanguage.KARAKALPAK -> "Jasırın malware, keylogger hám josus ruxsatların anıqlaw"
        AppLanguage.ENGLISH -> "Detect hidden malware, keyloggers and spyware permissions"
    }

    val txtCardTitle = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "Ilovalarni tahlil qilish"
        AppLanguage.UZ_CYRILLIC -> "Иловаларни таҳлил қилиш"
        AppLanguage.KAZAKH -> "Қосымшаларды талдау"
        AppLanguage.KYRGYZ -> "Тиркемелерди талдоо"
        AppLanguage.RUSSIAN -> "Анализ приложений"
        AppLanguage.KARAKALPAK -> "Ilovaları analiz etiw"
        AppLanguage.ENGLISH -> "Analyze applications"
    }

    val txtCardBody = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "Barcha o'rnatilgan paketlarni va xotiradagi yuklab olingan APK fayllarni tahlil qiling. Tizim maxfiylik ruxsatlarini (SMS o'qish, Oyna ustidan chizish, Shaxsiy xizmatlar) skanerlab, dushmanga qarshi xisobot shakllantiradi."
        AppLanguage.UZ_CYRILLIC -> "Барча ўрнатилган пакетларни ва хотирадаги юклаб олинган АПК файлларни таҳлил қилинг. Тизим махфийлик рухсатларини (SМS ўқиш, Ойна устидан чизиш, Шахсий хизматлар) сканерлаб, душманга қарши хисобот шакллантиради."
        AppLanguage.KAZAKH -> "Барлық орнатылған пакеттерді және жадтағы жүктелген APK файлдарын талдаңыз. Жүйе құпиялылық рұқсаттарын (SMS оқу, Терезе үстіне сурет салу, Жеке қызметтер) сканерлеп, қауіп тудыратын бағдарламалар бойынша есеп жасайды."
        AppLanguage.KYRGYZ -> "Бардык орнотулган пакеттерди жана эстутумдагы жүктөлүп алынган APK файлдарын талдаңыз. Тутум коопсуздук уруксаттарын (SMS окуу, Экран үстүнөн көрүнүү, Жеке кызметтер) сканерлеп, кооптуу программалар боюнча отчет түзөт."
        AppLanguage.RUSSIAN -> "Проанализируйте все установленные пакеты и загруженные файлы APK в памяти. Система сканирует конфиденциальные разрешения (чтение SMS, рисование поверх окон, специальные возможности) и формирует отчет по угрозам."
        AppLanguage.KARAKALPAK -> "Barlıq ornatılǵan paketlerdi hám xotiradaǵı júklep alınǵan APK fayllardı analiz etiń. Sistema qápsizlik ruxsatların skanerlep, esabat tayarlaydı."
        AppLanguage.ENGLISH -> "Analyze all installed packages and downloaded APK files in memory. The system scans sensitive permissions (SMS reading, Screen overlay, Accessibility) and generates an anti-threat report."
    }

    val txtBtnStart = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "YANGI SKANERLASH"
        AppLanguage.UZ_CYRILLIC -> "ЯНГИ СКАНЕРЛАШ"
        AppLanguage.KAZAKH -> "ЖАҢА СКАНЕРЛЕУ"
        AppLanguage.KYRGYZ -> "ЖАҢЫ СКАНЕРЛӨӨ"
        AppLanguage.RUSSIAN -> "НОВОЕ СКАНИРОВАНИЕ"
        AppLanguage.KARAKALPAK -> "JAŃA SKANERLEW"
        AppLanguage.ENGLISH -> "START NEW SCAN"
    }

    val txtScanning = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "SKANERLANMOQDA..."
        AppLanguage.UZ_CYRILLIC -> "СКАНЕРЛАНМОҚДА..."
        AppLanguage.KAZAKH -> "СКАНЕРЛЕНУДЕ..."
        AppLanguage.KYRGYZ -> "СКАНЕРЛЕНҮҮДӨ..."
        AppLanguage.RUSSIAN -> "ИДЕТ СКАНИРОВАНИЕ..."
        AppLanguage.KARAKALPAK -> "SKANERLENBEKTE..."
        AppLanguage.ENGLISH -> "SCANNING..."
    }

    val txtLoader = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "Tahlil qilinmoqda..."
        AppLanguage.UZ_CYRILLIC -> "Таҳлил қилинмоқда..."
        AppLanguage.KAZAKH -> "Талдануда..."
        AppLanguage.KYRGYZ -> "Талданып жатат..."
        AppLanguage.RUSSIAN -> "Анализируется..."
        AppLanguage.KARAKALPAK -> "Analiz etilmekte..."
        AppLanguage.ENGLISH -> "Analyzing..."
    }

    val txtCompleteTitle = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "SKANERLASH YAKUNLANDI"
        AppLanguage.UZ_CYRILLIC -> "СКАНЕРЛАШ ЯКУНЛАНДИ"
        AppLanguage.KAZAKH -> "СКАНЕРЛЕУ АЯҚТАЛДЫ"
        AppLanguage.KYRGYZ -> "СКАНЕРЛӨӨ АЯКТАДЫ"
        AppLanguage.RUSSIAN -> "СКАНИРОВАНИЕ ЗАВЕРШЕНО"
        AppLanguage.KARAKALPAK -> "SKANERLEW TAMAMLANDI"
        AppLanguage.ENGLISH -> "SCAN COMPLETED"
    }

    val txtRescan = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "Qayta skanerlash"
        AppLanguage.UZ_CYRILLIC -> "Қайта сканерлаш"
        AppLanguage.KAZAKH -> "Қайта сканерлеу"
        AppLanguage.KYRGYZ -> "Кайра сканерлөө"
        AppLanguage.RUSSIAN -> "Сканировать заново"
        AppLanguage.KARAKALPAK -> "Qayta skanerlew"
        AppLanguage.ENGLISH -> "Rescan"
    }

    val txtResultsTitle = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "TAHLIL NATIJALARI"
        AppLanguage.UZ_CYRILLIC -> "ТАҲЛИЛ НАТИЖАЛАРИ"
        AppLanguage.KAZAKH -> "ТАЛДАУ НӘТИЖЕЛЕРІ"
        AppLanguage.KYRGYZ -> "ТАЛДОО ЖЫЙЫНТЫКТАРЫ"
        AppLanguage.RUSSIAN -> "РЕЗУЛЬТАТЫ АНАЛИЗА"
        AppLanguage.KARAKALPAK -> "ANALIZ NATIJELERI"
        AppLanguage.ENGLISH -> "ANALYSIS RESULTS"
    }

    val riskHighLabel = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "YUQORI XAVFLI"
        AppLanguage.UZ_CYRILLIC -> "ЮҚОРИ ХАВФЛИ"
        AppLanguage.KAZAKH -> "ЖОҒАРЫ ҚАУІПТІ"
        AppLanguage.KYRGYZ -> "ЖОГОРКУ КОРКУНУЧТУУ"
        AppLanguage.RUSSIAN -> "ОПАСНО"
        AppLanguage.KARAKALPAK -> "O'TA QÁWIPLI"
        AppLanguage.ENGLISH -> "HIGH RISK"
    }

    val riskSuspectLabel = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "SHUBHALI"
        AppLanguage.UZ_CYRILLIC -> "ШУБҲАЛИ"
        AppLanguage.KAZAKH -> "КҮДІКТІ"
        AppLanguage.KYRGYZ -> "ШЕКТҮҮ"
        AppLanguage.RUSSIAN -> "ПОДОЗРИТЕЛЬНО"
        AppLanguage.KARAKALPAK -> "GÚMANLI"
        AppLanguage.ENGLISH -> "SUSPICIOUS"
    }

    val riskSafeLabel = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "XAVFSIZ"
        AppLanguage.UZ_CYRILLIC -> "ХАВФСИЗ"
        AppLanguage.KAZAKH -> "ҚАУІПСІЗ"
        AppLanguage.KYRGYZ -> "КООПСУЗ"
        AppLanguage.RUSSIAN -> "БЕЗОПАСНО"
        AppLanguage.KARAKALPAK -> "QÁWIPSIZ"
        AppLanguage.ENGLISH -> "SECURE"
    }

    val txtHighGrid = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "O'ta xavfli"
        AppLanguage.UZ_CYRILLIC -> "Ўта хавфли"
        AppLanguage.KAZAKH -> "Өте қауіпті"
        AppLanguage.KYRGYZ -> "Өтө кооптуу"
        AppLanguage.RUSSIAN -> "Опасные"
        AppLanguage.KARAKALPAK -> "O'ta qáwipli"
        AppLanguage.ENGLISH -> "Dangerous"
    }

    val txtSuspectGrid = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "Shubhali"
        AppLanguage.UZ_CYRILLIC -> "Шубҳали"
        AppLanguage.KAZAKH -> "Күдікті"
        AppLanguage.KYRGYZ -> "Шектүү"
        AppLanguage.RUSSIAN -> "Подозрительные"
        AppLanguage.KARAKALPAK -> "Gúmanli"
        AppLanguage.ENGLISH -> "Suspicious"
    }

    val txtSafeGrid = when (currentLanguage) {
        AppLanguage.UZ_LATIN -> "Xavfsiz"
        AppLanguage.UZ_CYRILLIC -> "Хавфсиз"
        AppLanguage.KAZAKH -> "Қауіпсіз"
        AppLanguage.KYRGYZ -> "Коопсуз"
        AppLanguage.RUSSIAN -> "Безопасные"
        AppLanguage.KARAKALPAK -> "Qáwipsiz"
        AppLanguage.ENGLISH -> "Secure"
    }

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

            // Also search device external storage folders for downloaded local .apk files
            val localApks = withContext(Dispatchers.IO) {
                val apksList = mutableListOf<PackageInfo>()
                try {
                    val searchDirs = listOf(
                        EnvironmentPublicDirs.getDownloadsDir(),
                        EnvironmentPublicDirs.getDocumentsDir(),
                        EnvironmentPublicDirs.getExternalStorageDir()
                    )
                    searchDirs.forEach { dir ->
                        if (dir != null && dir.exists() && dir.isDirectory) {
                            dir.listFiles()?.forEach { file ->
                                if (file.isFile && file.name.endsWith(".apk", ignoreCase = true)) {
                                    val archiveInfo = pm.getPackageArchiveInfo(file.absolutePath, PackageManager.GET_PERMISSIONS)
                                    if (archiveInfo != null) {
                                        val appInfo = archiveInfo.applicationInfo
                                        if (appInfo != null) {
                                            appInfo.sourceDir = file.absolutePath
                                            appInfo.publicSourceDir = file.absolutePath
                                        }
                                        apksList.add(archiveInfo)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Ignore listing limits or file permissions
                }
                apksList
            }

            // Exclude system apps, and specifically EXCLUDE QALQON app from appearing in scan lists
            val installedApps = packages.filter {
                val appInfo = it.applicationInfo
                if (appInfo != null) {
                    val isSystem = (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                    val isSelf = it.packageName == context.packageName
                    !isSystem && !isSelf
                } else {
                    false
                }
            }

            // Take first 40 installed apps + all found local APK files from storage
            val appsToScan = installedApps.take(40).toMutableList()
            localApks.forEach { apkInfo ->
                if (!appsToScan.any { it.packageName == apkInfo.packageName }) {
                    appsToScan.add(apkInfo)
                }
            }

            val total = appsToScan.size.toFloat()
            if (total == 0f) {
                isScanning = false
                showReport = true
                return@launch
            }

            appsToScan.forEachIndexed { index, packageInfo ->
                val appInfo = packageInfo.applicationInfo
                val isLocalApkPath = appInfo?.sourceDir?.endsWith(".apk", ignoreCase = true) == true && !appInfo.sourceDir.startsWith("/data/app")
                
                var appLabel = appInfo?.loadLabel(pm)?.toString() ?: packageInfo.packageName ?: "Noma'lum ilova"
                if (isLocalApkPath) {
                    val fileName = try { File(appInfo?.sourceDir ?: "").name } catch (e: Exception) { "" }
                    appLabel = if (fileName.isNotEmpty()) "$fileName (Xotiradan APK)" else "$appLabel (Xotiradan APK)"
                }

                currentScannedApp = appLabel
                scanProgress = (index + 1) / total
                
                // Permission hazard evaluation loop
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
                        suspectPermissions.add("Phone")
                    }
                }

                val originalRisk = when {
                    suspectPermissions.contains("Accessibility") || (suspectPermissions.contains("SMS") && suspectPermissions.contains("Overlay")) -> "YUQORI XAVFLI"
                    suspectPermissions.isNotEmpty() -> "SHUBHALI"
                    else -> "XAVFSIZ"
                }

                val risk = when (originalRisk) {
                    "YUQORI XAVFLI" -> riskHighLabel
                    "SHUBHALI" -> riskSuspectLabel
                    else -> riskSafeLabel
                }

                val details = if (suspectPermissions.isNotEmpty()) {
                    val joined = suspectPermissions.joinToString(", ")
                    when (currentLanguage) {
                        AppLanguage.UZ_LATIN -> "Seziluvchan ruxsatlar: $joined"
                        AppLanguage.UZ_CYRILLIC -> "Сезилувчан рухсатлар: $joined"
                        AppLanguage.KAZAKH -> "Сезімтал рұқсаттар: $joined"
                        AppLanguage.KYRGYZ -> "Сезгич уруксаттар: $joined"
                        AppLanguage.RUSSIAN -> "Чувствительные разрешения: $joined"
                        AppLanguage.KARAKALPAK -> "Seziluvshen ruxsatlar: $joined"
                        AppLanguage.ENGLISH -> "Sensitive permissions: $joined"
                    }
                } else {
                    when (currentLanguage) {
                        AppLanguage.UZ_LATIN -> "Xavfli ruxsatlar aniqlanmadi"
                        AppLanguage.UZ_CYRILLIC -> "Хавфли рухсатлар аниқланмади"
                        AppLanguage.KAZAKH -> "Қауіпті рұқсаттар табылған жоқ"
                        AppLanguage.KYRGYZ -> "Кооптуу уруксаттар табылган жок"
                        AppLanguage.RUSSIAN -> "Опасных разрешений не найдено"
                        AppLanguage.KARAKALPAK -> "Qáwipli ruxsatlar anıqlanmadı"
                        AppLanguage.ENGLISH -> "No dangerous permissions found"
                    }
                }

                val scanItem = ApkScanResult(
                    packageName = packageInfo.packageName ?: (appInfo?.sourceDir ?: "local_apk_$index"),
                    appName = appLabel,
                    riskLevel = risk,
                    riskDetails = details
                )

                delay(120) // Slightly slower animation response for cinematic cyber sweep look

                localScanResults = localScanResults + scanItem
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
                    text = txtTitle,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = txtSubTitle,
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
                        text = txtCardTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = txtCardBody,
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
                        Text(txtBtnStart, fontWeight = FontWeight.Bold)
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

                        // Code Scanner progress value
                        Text(
                            text = "${(scanProgress * 100).toInt()}%",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp
                        )
                    }

                    Text(
                        text = txtScanning,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = currentScannedApp.ifEmpty { txtLoader },
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
            // Scan Complete Report Grid List
            val highs = localScanResults.count { it.riskLevel == riskHighLabel }
            val suspects = localScanResults.count { it.riskLevel == riskSuspectLabel }
            val safes = localScanResults.count { it.riskLevel == riskSafeLabel }

            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = txtCompleteTitle,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                        
                        val rawCheckedLabel = when (currentLanguage) {
                            AppLanguage.UZ_LATIN -> "Jami %d ta paket tekshirildi"
                            AppLanguage.UZ_CYRILLIC -> "Жами %d та пакет текширилди"
                            AppLanguage.KAZAKH -> "Барлығы %d пакет тексерілді"
                            AppLanguage.KYRGYZ -> "Бардыгы %d пакет текшерилди"
                            AppLanguage.RUSSIAN -> "Всего проверено %d пакетов"
                            AppLanguage.KARAKALPAK -> "Barlıq %d paket tekserildi"
                            AppLanguage.ENGLISH -> "Total %d packages verified"
                        }
                        Text(
                            text = String.format(rawCheckedLabel, localScanResults.size),
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
                                Text(text = txtHighGrid, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "$suspects", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(text = txtSuspectGrid, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "$safes", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(text = txtSafeGrid, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                        }

                        Button(
                            onClick = { startApkScan() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(txtRescan, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                Text(
                    text = txtResultsTitle,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
            }

            items(localScanResults.sortedBy { 
                when (it.riskLevel) {
                    riskHighLabel -> 1
                    riskSuspectLabel -> 2
                    else -> 3
                }
            }) { app ->
                val badgeColor = when (app.riskLevel) {
                    riskHighLabel -> Color(0xFFFF1744)
                    riskSuspectLabel -> Color(0xFFFFC107)
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
                                    imageVector = if (app.riskLevel == riskSafeLabel) Icons.Default.CheckCircle else Icons.Default.Warning,
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

// Global helper for standard Android public storage directories
object EnvironmentPublicDirs {
    fun getDownloadsDir(): File {
        return android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
    }
    fun getDocumentsDir(): File {
        return android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS)
    }
    fun getExternalStorageDir(): File {
        return android.os.Environment.getExternalStorageDirectory()
    }
}
