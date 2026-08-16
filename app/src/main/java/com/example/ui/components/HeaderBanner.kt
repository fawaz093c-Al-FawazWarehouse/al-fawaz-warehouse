package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.util.LocalSoundManager

@Composable
fun HeaderBanner(
    onOpenAiClick: () -> Unit,
    onOpenJsonClick: () -> Unit,
    onOpenOrdersClick: () -> Unit = {}
) {
    val soundManager = LocalSoundManager.current
    val isSoundEnabled by soundManager.isSoundEnabled.collectAsState()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = EmeraldDark,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_logo_alfawaz_1786650981951),
                            contentDescription = "شعار مستودع الفواز للأدوية",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "مستودع الفواز للأدوية",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "حيث تبدأ الثقة • الوكالات الحصرية",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = GoldAccent
                        )
                    }
                }

                // Action Buttons: Sound, Order History, Gemini AI & JSON Update
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Sound Toggle Button
                    IconButton(
                        onClick = {
                            soundManager.toggleSound()
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSoundEnabled) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.3f))
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = if (isSoundEnabled) "كتم الصوت" else "تفعيل أصوات النقر",
                            tint = if (isSoundEnabled) GoldAccent else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            soundManager.playClick()
                            onOpenOrdersClick()
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "سجل الطلبيات",
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            soundManager.playClick()
                            onOpenAiClick()
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoldAccent)
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "المساعد الذكي Gemini",
                            tint = EmeraldDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            soundManager.playClick()
                            onOpenJsonClick()
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldPrimary)
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "تحديث JSON",
                            tint = GoldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

