package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberNeonDivider
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.QalqonLogo
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun PermissionOnboardingScreen(
    onPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasSms by remember { mutableStateOf(false) }
    var hasPhoneState by remember { mutableStateOf(false) }
    var hasOverlay by remember { mutableStateOf(false) }
    var hasNotification by remember { mutableStateOf(false) }

    // Synchronize current state when screen launches and every time user resumes
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkCurrentPermissions(context) { sms, phone, overlay, notification ->
                    hasSms = sms
                    hasPhoneState = phone
                    hasOverlay = overlay
                    hasNotification = notification
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Auto-advance if all requirements are met
    LaunchedEffect(hasSms, hasPhoneState, hasOverlay, hasNotification) {
        if (hasSms && hasPhoneState && hasOverlay && hasNotification) {
            onPermissionsGranted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Large Cyber Shield Icon loaded through QalqonLogo fallback system
        Surface(
            modifier = Modifier.size(90.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            shape = CircleShape,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                QalqonLogo(
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        Text(
            text = "XAVFSIZLIK SOZLAMALARI",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Ilovaning barcha himoya qatlamlari (Spam bloklagich va Parental-Control) to'g'ri ishlashi uchun quyidagi ruxsatnomalarni faollashtiring. Bu mutlaqo xavfsiz.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 17.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        CyberNeonDivider(color = MaterialTheme.colorScheme.secondary)

        PermissionStepCard(
            title = "1. SMS va Qo'ng'iroqlarni aniqlash",
            description = "Kiruvchi spam SMS-xabarlarni va shubhali telefon qo'ng'iroqlarini filtrga olish va tahlil qilish uchun talab qilinadi.",
            icon = Icons.Default.Message,
            isGranted = hasSms && hasPhoneState,
            color = MaterialTheme.colorScheme.primary,
            onClick = {
                val activity = context as? Activity ?: return@PermissionStepCard
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.requestPermissions(
                        arrayOf(
                            android.Manifest.permission.RECEIVE_SMS,
                            android.Manifest.permission.READ_PHONE_STATE,
                            android.Manifest.permission.READ_CALL_LOG
                        ),
                        101
                    )
                }
            }
        )

        PermissionStepCard(
            title = "2. Ekran ustidan chizish (Overlay)",
            description = "Spam xavfi aniqlanganda ekranda to'liq ogohlantirish oynasini (SpamWarningActivity) chiqarish uchun ruxsat bering.",
            icon = Icons.Default.FlipToFront,
            isGranted = hasOverlay,
            color = MaterialTheme.colorScheme.secondary,
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                }
            }
        )

        PermissionStepCard(
            title = "3. Tizim xabarnomalari",
            description = "Telefoningiz doimiy 'Asadbei Shield' xizmati tomonidan himoyalanganligini bildirish va ogohlantirishlarni olish uchun kerak.",
            icon = Icons.Default.Notifications,
            isGranted = hasNotification,
            color = MaterialTheme.colorScheme.tertiary,
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val activity = context as? Activity ?: return@PermissionStepCard
                    activity.requestPermissions(
                        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                        102
                    )
                } else {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                }
            }
        )

        // Visual animation simulator walkthrough block
        GlassmorphicCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Maslahat",
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "QO'LLANMA: Rangli ruxsat berish tugmasini bosing va tizim so'rovlarida 'Ruxsat berish' bandini tanlang.",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // Instantly let user proceed regardless to prevent getting stuck under any condition
                onPermissionsGranted()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("refresh_permissions_button")
        ) {
            Text(
                text = "HIMOYA REJIMINI BOSHLASH",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp
            )
        }

        // Secondary Skip bypass button to double safeguard the screen against locks
        TextButton(
            onClick = { onPermissionsGranted() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "O'TKAZIB YUBORISH (RUXSATLARSIZ DAVOM ETISH)",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun PermissionStepCard(
    title: String,
    description: String,
    icon: ImageVector,
    isGranted: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isGranted) MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f) else color.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                color = (if (isGranted) MaterialTheme.colorScheme.secondary else color).copy(alpha = 0.1f),
                shape = CircleShape,
                border = BorderStroke(1.dp, if (isGranted) MaterialTheme.colorScheme.secondary else color)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isGranted) Icons.Default.Check else icon,
                        contentDescription = "Icon",
                        tint = if (isGranted) MaterialTheme.colorScheme.secondary else color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
                if (!isGranted) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = color.copy(alpha = 0.15f),
                            contentColor = color
                        ),
                        border = BorderStroke(1.dp, color),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(text = "RUXSAT BERISH", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✓ FAQOL VA HIMOYADA",
                        color = Color(0xFF00E676),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

fun checkCurrentPermissions(
    context: Context,
    onResult: (sms: Boolean, phone: Boolean, overlay: Boolean, notification: Boolean) -> Unit
) {
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
        onResult(hasSms, hasPhone && hasCallLog, hasOverlay, hasNotification)
    } else {
        onResult(true, true, true, true)
    }
}
