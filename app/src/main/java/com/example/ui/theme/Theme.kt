package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFF00E5FF),
        secondary = Color(0xFF00E676),
        tertiary = Color(0xFFFFC107),
        background = Color(0xFF070B19),
        surface = Color(0xFF0D132D),
        surfaceVariant = Color(0xFF131A38),
        onBackground = Color.White,
        onSurface = Color.White,
        onSurfaceVariant = Color.LightGray
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Color(0xFF00ACC1),
        secondary = Color(0xFF43A047),
        tertiary = Color(0xFFFFB300),
        background = Color(0xFFF4F6FA),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFE3E7F0),
        onBackground = Color(0xFF070B19),
        onSurface = Color(0xFF070B19),
        onSurfaceVariant = Color(0xFF5C6F84)
    )

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to prioritize our elegant custom Day/Night design
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
