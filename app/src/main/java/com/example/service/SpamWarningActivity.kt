package com.example.service

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme

class SpamWarningActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val incomingNumber = intent.getStringExtra("INCOMING_NUMBER") ?: "+998 (99) 001-02-03"
        val spamLabel = intent.getStringExtra("SPAM_LABEL") ?: "Noma'lum Spam / Firibgarlik"

        setContent {
            MyApplicationTheme {
                SpamWarningScreen(
                    number = incomingNumber,
                    label = spamLabel,
                    onBlock = {
                        finish()
                    },
                    onContinue = {
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun SpamWarningScreen(
    number: String,
    label: String,
    onBlock: () -> Unit,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B19))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glowing Background Core
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFF1744), Color(0xFF00E5FF))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .testTag("spam_warning_card"),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F162E).copy(alpha = 0.85f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Neon Blinking Warning Badge
                Surface(
                    color = Color(0xFFFF1744).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF1744)),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Ogohlantirish",
                            tint = Color(0xFFFF1744),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "XAVF ANIQLANDI",
                            color = Color(0xFFFF1744),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Text(
                    text = "SPAM CHAQIRUV ANIQLANDI!",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "⚠️ Bu raqam spam sifatida belgilangan. Javob berishga rozimisiz?",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Block detail info
                Surface(
                    color = Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = number,
                            color = Color(0xFF00E5FF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Turi: $label",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }

                // Buttons: Block vs Continue
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onContinue,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.1f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .testTag("spam_continue_button")
                    ) {
                        Text(text = "Davom etish", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Button(
                        onClick = onBlock,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF1744),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("spam_block_button")
                    ) {
                        Text(text = "Bloklash", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
