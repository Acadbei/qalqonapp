package com.example.service

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme

class WebsiteBlockActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val blockedDomain = intent.getStringExtra("BLOCKED_DOMAIN") ?: "taqiqlangan-sayt.com"
        val blockedReason = intent.getStringExtra("BLOCKED_REASON") ?: "Xavfsizlik yorlig'i"

        setContent {
            MyApplicationTheme {
                WebsiteBlockScreen(
                    domain = blockedDomain,
                    reason = blockedReason,
                    onGoHome = {
                        // Direct the user strictly to the system's Home launcher
                        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_HOME)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(homeIntent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun WebsiteBlockScreen(
    domain: String,
    reason: String,
    onGoHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B19))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // High Contrast Warning Card with Glassmorphic visual structure
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFF1744), Color(0xFF00E5FF))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .testTag("website_block_card"),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F162E).copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large Glowing Shield Warning Icon
                Surface(
                    color = Color(0xFFFF1744).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFFF1744)),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Taqiq belgi",
                            tint = Color(0xFFFF1744),
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                Text(
                    text = "KIRISH TAQIQLANDI",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "⛔ Ushbu sayt administrator tomonidan taqiqlangan",
                    color = Color(0xFFFF1744),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Info card showing domains
                Surface(
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = domain,
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Sabab: $reason",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Redirect home button to break browser loops
                Button(
                    onClick = onGoHome,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E5FF),
                        contentColor = Color(0xFF070B19)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("block_go_home_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Ortga Qaytish",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Xavfsiz bosh ekranga yo'naltirish",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
