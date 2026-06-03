package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppLanguage
import com.example.ui.components.Localization
import com.example.ui.components.QalqonLogo
import kotlinx.coroutines.delay

@Composable
fun LogoSplashScreen(
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onDismissSplash: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B19)), // Premium deep space blue dark backplane
        contentAlignment = Alignment.Center
    ) {
        // Glowing Ambient Background Ellipse
        Box(
            modifier = Modifier
                .size(320.dp)
                .alpha(0.18f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00E5FF),
                            Color.Transparent
                        )
                    )
                )
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(1200)) + expandVertically(animationSpec = tween(1200)),
            exit = fadeOut(animationSpec = tween(800))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Empty space for structural balance
                Spacer(modifier = Modifier.height(16.dp))

                // Mid segment containing logo & text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(130.dp),
                        color = Color(0xFF0D132D).copy(alpha = 0.6f),
                        shape = CircleShape,
                        border = BorderStroke(2.dp, Color(0xFF00E5FF))
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            QalqonLogo(
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(70.dp)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "QALQON",
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                            letterSpacing = 3.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Kriminologiya Taqdiqot Instituti© 2026",
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Bottom segment containing Language selection and Enter button
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "TILNI TANLANG / ВЫБЕРИТЕ ЯЗЫК",
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )

                    // LazyRow of Languages beautifully framed
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(AppLanguage.values()) { lang ->
                            val isSelected = lang == currentLanguage
                            val borderCol = if (isSelected) Color(0xFF00E676) else Color.White.copy(alpha = 0.12f)
                            val bgCol = if (isSelected) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFF131A38).copy(alpha = 0.5f)
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(32.dp))
                                    .background(bgCol)
                                    .border(BorderStroke(1.2.dp, borderCol), RoundedCornerShape(32.dp))
                                    .clickable { onLanguageChange(lang) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (lang) {
                                        AppLanguage.UZ_LATIN -> "🇺🇿 O'zbek"
                                        AppLanguage.UZ_CYRILLIC -> "🇺🇿 Ўзбек"
                                        AppLanguage.KAZAKH -> "🇰🇿 Қазақ"
                                        AppLanguage.KYRGYZ -> "🇰🇬 Кыргыз"
                                        AppLanguage.RUSSIAN -> "🇷🇺 Русс"
                                        AppLanguage.KARAKALPAK -> "🇺🇿 Қарақалпақ"
                                        AppLanguage.ENGLISH -> "🇬🇧 English"
                                    },
                                    color = if (isSelected) Color(0xFF00E676) else Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val buttonLabel = when (currentLanguage) {
                        AppLanguage.UZ_LATIN -> "DAVOM ETISH"
                        AppLanguage.UZ_CYRILLIC -> "ДАВОМ ЭТИШ"
                        AppLanguage.KAZAKH -> "ЖАЛҒАСТЫРУ"
                        AppLanguage.KYRGYZ -> "ДАМ БАШТОО"
                        AppLanguage.RUSSIAN -> "ПРОДОЛЖИТЬ"
                        AppLanguage.KARAKALPAK -> "DAVOM ETISH"
                        AppLanguage.ENGLISH -> "CONTINUE"
                    }

                    Button(
                        onClick = onDismissSplash,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E5FF),
                            contentColor = Color(0xFF070B19)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(text = buttonLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }
}
