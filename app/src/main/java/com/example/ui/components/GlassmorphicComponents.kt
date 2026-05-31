package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    val cardBgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)

    Surface(
        modifier = modifier
            .background(Color.Transparent)
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = borderColor
                ),
                shape = RoundedCornerShape(cornerRadius)
            ),
        color = cardBgColor,
        shape = RoundedCornerShape(cornerRadius),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun CyberNeonDivider(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00E5FF)
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        color,
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun QalqonLogo(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            // Try loading 1.png from assets
            context.assets.open("1.png").use { inputStream ->
                bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                loaded = true
            }
        } catch (e: Exception) {
            // Try fallback from files dir
            try {
                val file = java.io.File(context.filesDir, "1.png")
                if (file.exists()) {
                    bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                    loaded = true
                }
            } catch (ex: Exception) {
                // Ignore fallback exceptions
            }
        }
    }

    if (loaded && bitmap != null) {
        androidx.compose.foundation.Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "QalqonApp Logo",
            modifier = modifier
        )
    } else {
        Icon(
            painter = painterResource(id = com.example.R.drawable.ic_logo_shield),
            contentDescription = "QalqonApp Logo",
            tint = tint,
            modifier = modifier
        )
    }
}

