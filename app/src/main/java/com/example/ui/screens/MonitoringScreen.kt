package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BlockedDomain
import com.example.ui.components.CyberNeonDivider
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.Localization
import com.example.ui.components.AppLanguage
import kotlinx.coroutines.flow.Flow

@Composable
fun MonitoringScreen(
    currentLanguage: AppLanguage,
    isAdminUnlocked: Boolean,
    blockedDomainsFlow: Flow<List<BlockedDomain>>,
    isAccessibilityServiceActive: Boolean,
    onAddDomain: (String, String) -> Unit,
    onDeleteDomain: (BlockedDomain) -> Unit
) {
    val context = LocalContext.current
    val blockedDomains by blockedDomainsFlow.collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }
    var domainInput by remember { mutableStateOf("") }
    var reasonInput by remember { mutableStateOf("") }

    var showGuide by remember { mutableStateOf(true) }

    if (showAddDialog && isAdminUnlocked) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(Localization.getString(currentLanguage, "add_domain_title"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            containerColor = MaterialTheme.colorScheme.surface,
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Masalan: betting-link.uz, casino-game.net.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    OutlinedTextField(
                        value = domainInput,
                        onValueChange = { domainInput = it },
                        label = { Text("Domen manzili") },
                        placeholder = { Text("site.com") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_domain_input")
                    )

                    OutlinedTextField(
                        value = reasonInput,
                        onValueChange = { reasonInput = it },
                        label = { Text("Sababi") },
                        placeholder = { Text("Bloklash sababi") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_domain_reason_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (domainInput.trim().isNotEmpty()) {
                            onAddDomain(domainInput.trim().lowercase(), reasonInput.trim().ifEmpty { "Taqiqlangan sayt" })
                            domainInput = ""
                            reasonInput = ""
                            showAddDialog = false
                            Toast.makeText(context, "Domen bloklandi!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Text(Localization.getString(currentLanguage, "save"), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(Localization.getString(currentLanguage, "cancel"), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        )
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
                    text = Localization.getString(currentLanguage, "web_filter_title"),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = Localization.getString(currentLanguage, "web_filter_sub"),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }

        // Global admin status notification
        if (!isAdminUnlocked) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.onErrorContainer)
                        Text(
                            text = Localization.getString(currentLanguage, "admin_only_overlay"),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Accessibility Service controller banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        BorderStroke(1.dp, if (isAccessibilityServiceActive) MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(36.dp),
                                color = (if (isAccessibilityServiceActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error).copy(alpha = 0.1f),
                                shape = CircleShape
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isAccessibilityServiceActive) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = "Status",
                                        tint = if (isAccessibilityServiceActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = Localization.getString(currentLanguage, "web_filter"),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (isAccessibilityServiceActive) "Xizmat faol holatda" else "Maxsus ruxsat berilmagan",
                                    color = if (isAccessibilityServiceActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Sozlamalarni ochib bo'lmadi", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAccessibilityServiceActive) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f) else MaterialTheme.colorScheme.primary,
                                contentColor = if (isAccessibilityServiceActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = if (isAccessibilityServiceActive) "Tuzatish" else "Yoqish",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Quick clickable toggle for Guide
                    TextButton(
                        onClick = { showGuide = !showGuide },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = if (showGuide) "- Faollashtirish bo'yicha yo'riqnomani berkitish" else "+ Faollashtirish bo'yicha yo'riqnomani ochish",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Detailed Instruction walkthrough step card
        if (showGuide && !isAccessibilityServiceActive) {
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "🌐 BRAUZER FILTRINI FAOL QILISH YO'RIG'NOMASI",
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )

                        Text(
                            text = "Google Chrome va boshqa brauzerlarda taqiqlangan saytlarni aniqlash uchun quyidagi qadamlarni bajaring:",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        StepItem(
                            stepNum = "1",
                            text = "Yoqish tugmasini bosing."
                        )
                        StepItem(
                            stepNum = "2",
                            text = "Yuklab olingan xizmatlar (Downloaded Services) bandini toping."
                        )
                        StepItem(
                            stepNum = "3",
                            text = "'Qalqon' xizmatini faollashtiring (Yoqish / ON)."
                        )
                    }
                }
            }
        }

        if (isAdminUnlocked) {
            // Manage domains panel
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)), RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(Localization.getString(currentLanguage, "add_domain"), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Taqiqlangan saytlarni qo'shish", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f), fontSize = 10.sp)
                        }

                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier.height(34.dp).testTag("add_domain_fab")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Qo'shish", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Section listing blocked sites
        item {
            Text(
                text = "${Localization.getString(currentLanguage, "blocked_list")} (${blockedDomains.size})",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (blockedDomains.isEmpty()) {
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Public, contentDescription = "Websites", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Bloklangan saytlar mavjud emas.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(blockedDomains) { domain ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                            shape = CircleShape
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Block, contentDescription = "Blocked", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            }
                        }

                        Column {
                            Text(text = domain.domain, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = domain.reason, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.82f), fontSize = 11.sp)
                        }
                    }

                    if (isAdminUnlocked) {
                        IconButton(
                            onClick = { onDeleteDomain(domain) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "O'chirish", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepItem(stepNum: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier.size(20.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = stepNum, color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            fontSize = 11.sp,
            lineHeight = 15.sp
        )
    }
}
