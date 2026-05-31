package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.example.ui.components.CyberNeonDivider
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.Localization
import com.example.ui.components.AppLanguage

@Composable
fun SettingsScreen(
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    isAdminUnlocked: Boolean,
    onAdminUnlockedChange: (Boolean) -> Unit,
    currentPin: String,
    onSavePin: (String) -> Unit,
    onClearDatabase: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var oldPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }

    var lockPinInput by remember { mutableStateOf("") }

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
                    text = Localization.getString(currentLanguage, "admin_settings"),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = Localization.getString(currentLanguage, "admin_settings_sub"),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }

        // Language settings item
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🌐 " + Localization.getString(currentLanguage, "language"),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Ilova tilini tanlang / Select Language:",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(AppLanguage.values()) { lang ->
                            val isSelected = lang == currentLanguage
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { onLanguageChange(lang) }
                            ) {
                                Text(
                                    text = lang.displayName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Check if locked
        if (!isAdminUnlocked) {
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(54.dp),
                            color = Color(0xFFFFC107).copy(alpha = 0.1f),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color(0xFFFFC107))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Text(
                            text = "Administrator Bo'limi Qulflangan",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Text(
                            text = "Ma'muriy sozlamalarga ruxsat olish uchun PIN-kod kiritishingiz so'raladi.",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        OutlinedTextField(
                            value = lockPinInput,
                            onValueChange = { lockPinInput = it },
                            label = { Text("PIN Kod") },
                            placeholder = { Text("••••") },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("unlocked_pin_input")
                        )

                        Button(
                            onClick = {
                                if (lockPinInput == currentPin) {
                                    onAdminUnlockedChange(true)
                                    lockPinInput = ""
                                    Toast.makeText(context, "Muvaffaqiyatli kirildi!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "PIN kodi xato!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("admin_unlock_button")
                        ) {
                            Text("KIRISH", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            // Logged in features
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.LockOpen, contentDescription = "Unlocked", tint = MaterialTheme.colorScheme.secondary)
                                Text(
                                    text = "PIN Kodni o'zgartirish",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            TextButton(onClick = { onAdminUnlockedChange(false) }) {
                                Text("Bloklash", color = MaterialTheme.colorScheme.error, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        CyberNeonDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))

                        OutlinedTextField(
                            value = oldPinInput,
                            onValueChange = { oldPinInput = it },
                            label = { Text("Eski PIN") },
                            placeholder = { Text("••••") },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("old_pin_input")
                        )

                        OutlinedTextField(
                            value = newPinInput,
                            onValueChange = { newPinInput = it },
                            label = { Text("Yangi PIN") },
                            placeholder = { Text("••••") },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("new_pin_input")
                        )

                        OutlinedTextField(
                            value = confirmPinInput,
                            onValueChange = { confirmPinInput = it },
                            label = { Text("Yangi PINni tasdiqlang") },
                            placeholder = { Text("••••") },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("confirm_pin_input")
                        )

                        Button(
                            onClick = {
                                if (oldPinInput != currentPin) {
                                    Toast.makeText(context, "Eski PIN noto'g'ri!", Toast.LENGTH_SHORT).show()
                                } else if (newPinInput.length < 4) {
                                    Toast.makeText(context, "Yangi PIN kamida 4 xonali bo'lishi lozim!", Toast.LENGTH_SHORT).show()
                                } else if (newPinInput != confirmPinInput) {
                                    Toast.makeText(context, "Yangi PIN kodi tasdig'i bir xil emas!", Toast.LENGTH_SHORT).show()
                                } else {
                                    onSavePin(newPinInput.trim())
                                    oldPinInput = ""
                                    newPinInput = ""
                                    confirmPinInput = ""
                                    onAdminUnlockedChange(false)
                                    Toast.makeText(context, "PIN kod muvaqqiyatli yangilandi va bo'lim qulflandi!", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("save_pin_button")
                        ) {
                            Text("PAROLNI YANGILASH", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            // System cleaning / cache
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f)), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Clear DB", tint = MaterialTheme.colorScheme.error)
                            Text(
                                text = "Ma'lumotlar bazasini tozalash",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "Ushbu tugma bosilganda spam ro'yxati va bloklangan daxldor veb filtrlar tozalanishi va asl zavod holatiga qaytariladi.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Button(
                            onClick = {
                                onClearDatabase()
                                onAdminUnlockedChange(false)
                                Toast.makeText(context, "Ma'lumotlar bazasi tozalandi!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF1744),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("clear_db_button")
                        ) {
                            Text("MAL'UMOTLARNI TOZALASH (RESET)", fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Theme Settings (Day/Night)
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "🎨 INTERFEYS MAVZUSI (KUN / TUN)",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Ilovani oq (kunduzgi) yoki qora (tungi) rejimda ishlatish uchun quyidagi tugmani bosing.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isDarkMode) "TUNGI (QORA) REJIM" else "KUNDUZGI (OQ) REJIM",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp
                        )
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = onToggleDarkMode,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.testTag("theme_switch")
                        )
                    }
                }
            }
        }

        // Device system information
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "📱 QURILMA MA'LUMOTLARI",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    DeviceInfoRow(label = "Model:", value = android.os.Build.MODEL)
                    DeviceInfoRow(label = "Android Versiya:", value = android.os.Build.VERSION.RELEASE)
                    DeviceInfoRow(label = "SDK Daraqasi:", value = "${android.os.Build.VERSION.SDK_INT}")
                    DeviceInfoRow(label = "Tizim:", value = "Qalqon Engine v1.0")
                }
            }
        }
    }
}

@Composable
fun DeviceInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 11.sp)
        Text(text = value, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium, fontSize = 11.sp)
    }
}
