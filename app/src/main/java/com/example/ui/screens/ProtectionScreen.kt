package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SpamLog
import com.example.data.SpamNumber
import com.example.ui.components.CyberNeonDivider
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.Localization
import com.example.ui.components.AppLanguage
import kotlinx.coroutines.flow.Flow

@Composable
fun ProtectionScreen(
    currentLanguage: AppLanguage,
    isAdminUnlocked: Boolean,
    spamNumbersFlow: Flow<List<SpamNumber>>,
    spamLogsFlow: Flow<List<SpamLog>>,
    spamKeywordsFlow: Flow<List<String>>,
    remoteUrl: String,
    onSaveRemoteUrl: (String) -> Unit,
    onSyncRemote: () -> Unit,
    onAddSpamNumber: (String, String) -> Unit,
    onDeleteSpamNumber: (SpamNumber) -> Unit,
    onClearSpamLogs: () -> Unit,
    onImportLocalTxt: (String) -> Unit,
    onAddSpamKeyword: (String) -> Unit,
    onRemoveSpamKeyword: (String) -> Unit
) {
    val context = LocalContext.current
    val spamNumbers by spamNumbersFlow.collectAsState(initial = emptyList())
    val spamLogs by spamLogsFlow.collectAsState(initial = emptyList())
    val spamKeywords by spamKeywordsFlow.collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }
    var phoneInput by remember { mutableStateOf("") }
    var labelInput by remember { mutableStateOf("") }

    var showSyncPanel by remember { mutableStateOf(false) }
    var urlInput by remember { mutableStateOf(remoteUrl) }

    var showLocalImportPanel by remember { mutableStateOf(false) }
    var localTxtInput by remember { mutableStateOf("") }
    var keywordInput by remember { mutableStateOf("") }

    if (showAddDialog && isAdminUnlocked) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(Localization.getString(currentLanguage, "add_spam_num"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            containerColor = MaterialTheme.colorScheme.surface,
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text(Localization.getString(currentLanguage, "phone_num")) },
                        placeholder = { Text(Localization.getString(currentLanguage, "num_placeholder")) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_spam_phone_input")
                    )

                    OutlinedTextField(
                        value = labelInput,
                        onValueChange = { labelInput = it },
                        label = { Text(Localization.getString(currentLanguage, "label_name")) },
                        placeholder = { Text(Localization.getString(currentLanguage, "label_placeholder")) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_spam_label_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (phoneInput.trim().isNotEmpty()) {
                            onAddSpamNumber(phoneInput.trim(), labelInput.trim().ifEmpty { "Spam" })
                            phoneInput = ""
                            labelInput = ""
                            showAddDialog = false
                            Toast.makeText(context, Localization.getString(currentLanguage, "success_login"), Toast.LENGTH_SHORT).show()
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
                    text = Localization.getString(currentLanguage, "sms_call_title"),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = Localization.getString(currentLanguage, "sms_call_sub"),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }

        // Global admin block warning banner
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

        if (isAdminUnlocked) {
            // Action controls
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.weight(1.3f).height(48.dp).testTag("add_spam_number_fab"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(Localization.getString(currentLanguage, "add_spam_btn"), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { showSyncPanel = !showSyncPanel },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Sync", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(Localization.getString(currentLanguage, "url_sync"), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }

            // Expanded CPanel sync setup block
            if (showSyncPanel) {
                item {
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "URL SPAM TXT MANZILI",
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "TXT URL:",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )

                            OutlinedTextField(
                                value = urlInput,
                                onValueChange = { urlInput = it },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00E5FF),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                                    focusedLabelColor = Color(0xFF00E5FF)
                                ),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("cpanel_url_input")
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = {
                                        onSaveRemoteUrl(urlInput.trim())
                                        Toast.makeText(context, "URL Saqlandi!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131A38)),
                                    modifier = Modifier.weight(1f).border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(Localization.getString(currentLanguage, "save"), fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        onSyncRemote()
                                        showSyncPanel = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676), contentColor = Color(0xFF070B19)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Sinx", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sinxronlash", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Local TXT file import block
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLocalImportPanel = !showLocalImportPanel }
                        .border(BorderStroke(0.5.dp, Color.White.copy(alpha = 0.08f)), RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131A38).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Create, contentDescription = "Txt Import", tint = Color(0xFFFFC107))
                            Column {
                                Text(Localization.getString(currentLanguage, "import_txt"), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Qurilmadagi matnlarni nusxalab olish orqali", color = Color.LightGray.copy(alpha = 0.5f), fontSize = 10.sp)
                            }
                        }
                        Icon(
                            imageVector = if (showLocalImportPanel) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            if (showLocalImportPanel) {
                item {
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "TXT FORMAT",
                                color = Color(0xFFFFC107),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Matnni nusxalang:",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )

                            OutlinedTextField(
                                value = localTxtInput,
                                onValueChange = { localTxtInput = it },
                                placeholder = { Text("nomer:+998901112233,Qallob\nnomer:+998934445566,Firibgar\nsayt:dangerous-link.uz,Virus") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFFC107),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                                ),
                                maxLines = 6,
                                modifier = Modifier.fillMaxWidth().height(100.dp).testTag("local_txt_input")
                            )

                            Button(
                                onClick = {
                                    if (localTxtInput.trim().isNotEmpty()) {
                                        onImportLocalTxt(localTxtInput.trim())
                                        localTxtInput = ""
                                        showLocalImportPanel = false
                                        Toast.makeText(context, "Muvaffaqiyatli import qilindi!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107), contentColor = Color(0xFF070B19)),
                                modifier = Modifier.fillMaxWidth().height(38.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Import", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 4. SMS Spam Keywords Filters
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Spam Keywords",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = Localization.getString(currentLanguage, "keywords"),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = Localization.getString(currentLanguage, "keywords_desc"),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Add keyword row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = keywordInput,
                                onValueChange = { keywordInput = it },
                                placeholder = { Text(Localization.getString(currentLanguage, "add_keyword") + "...", fontSize = 12.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                ),
                                singleLine = true,
                                modifier = Modifier.weight(1f).height(48.dp)
                            )

                            Button(
                                onClick = {
                                    val clean = keywordInput.trim()
                                    if (clean.isNotEmpty()) {
                                        onAddSpamKeyword(clean)
                                        keywordInput = ""
                                        Toast.makeText(context, "$clean kalit so'zi qo'shildi!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(48.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Qo'shish", modifier = Modifier.size(18.dp))
                            }
                        }

                        // Custom horizontal layout of active keywords chips
                        if (spamKeywords.isEmpty()) {
                            Text(
                                text = "Kalit so'zlar bo'sh.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        } else {
                            androidx.compose.foundation.lazy.LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(spamKeywords) { word ->
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
                                        modifier = Modifier.clickable { 
                                            onRemoveSpamKeyword(word)
                                            Toast.makeText(context, "$word o'chirildi", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = word,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "O'chirish",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Spam numbers listing
        item {
            Text(
                text = "${Localization.getString(currentLanguage, "spam_list")} (${spamNumbers.size})",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (spamNumbers.isEmpty()) {
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Empty", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(Localization.getString(currentLanguage, "spam_list_empty"), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(spamNumbers) { number ->
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
                                Icon(imageVector = Icons.Default.Block, contentDescription = "Spam", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            }
                        }

                        Column {
                            Text(text = number.phoneNumber, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = number.label, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f), fontSize = 11.sp)
                        }
                    }

                    if (isAdminUnlocked) {
                        IconButton(
                            onClick = { onDeleteSpamNumber(number) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "O'chirish", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Spam Alert Block histories section
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${Localization.getString(currentLanguage, "recent_activity")} (${spamLogs.size})",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )

                if (spamLogs.isNotEmpty() && isAdminUnlocked) {
                    TextButton(onClick = onClearSpamLogs) {
                        Text("Tozalash", color = Color(0xFFFF1744), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (spamLogs.isEmpty()) {
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.List, contentDescription = "Empty", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(Localization.getString(currentLanguage, "no_logs"), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(spamLogs) { log ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val iconColor = if (log.type == "SMS") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                        val iconImage = when (log.type) {
                            "SMS" -> Icons.Default.Sms
                            "QO'NG'IROQ" -> Icons.Default.Phone
                            else -> Icons.Default.Language
                        }
                        Icon(
                            imageVector = iconImage,
                            contentDescription = log.type,
                            tint = iconColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = log.sender, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Surface(
                                    color = Color(0xFF00E5FF).copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = log.type,
                                        color = Color(0xFF00E5FF),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = log.message, color = Color.LightGray.copy(alpha = 0.7f), fontSize = 11.sp, lineHeight = 15.sp)
                        }
                    }

                    Text(
                        text = log.actionTaken,
                        color = if (log.actionTaken == "BLOKLANDI") Color(0xFFFF1744) else Color(0xFF00E676),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
