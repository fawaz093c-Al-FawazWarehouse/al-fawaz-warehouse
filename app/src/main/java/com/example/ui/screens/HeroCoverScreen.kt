package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CartItem
import com.example.data.model.WarehouseOffer
import com.example.ui.components.AboutWarehouseDialog
import com.example.ui.components.OffersDialog
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.util.LocalSoundManager

@Composable
fun HeroCoverScreen(
    cartItems: List<CartItem>,
    offers: List<WarehouseOffer>,
    isDarkModeOverride: Boolean?,
    onBrowseDrugs: () -> Unit,
    onOpenCompanies: () -> Unit,
    onOpenAiChat: () -> Unit,
    onOpenJsonUpdate: () -> Unit,
    onOpenOrdersScreen: () -> Unit,
    onOpenCartScreen: () -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onSelectAgencyOffer: (String) -> Unit
) {
    val context = LocalContext.current
    val soundManager = LocalSoundManager.current
    val systemIsDark = isSystemInDarkTheme()
    val isDark = isDarkModeOverride ?: systemIsDark

    var showAboutDialog by remember { mutableStateOf(false) }
    var showOffersDialog by remember { mutableStateOf(false) }

    val cartTotalCount = cartItems.sumOf { it.quantity }

    // Pulsing subtle logo animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // High quality product hero background image
        Image(
            painter = painterResource(id = R.drawable.hero_warehouse_cover_1786701682928),
            contentDescription = "غلاف مستودع الفواز",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Luxury Dark Gradient Overlay (Transparent at top to Deep Emerald/Dark at bottom)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x77071610),
                            Color(0xAA071A12),
                            Color(0xEE061811),
                            Color(0xFF04100B)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Main Content Layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Luxury Control Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Controls: Dark Mode & Sound & Orders
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassIconButton(
                        icon = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "الوضع الليلي",
                        onClick = {
                            soundManager.playClick()
                            onToggleDarkMode(systemIsDark)
                        }
                    )

                    val isSoundOn by soundManager.isSoundEnabled.collectAsState()
                    GlassIconButton(
                        icon = if (isSoundOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "المؤثرات الصوتية",
                        tint = if (isSoundOn) GoldAccent else Color.White.copy(alpha = 0.7f),
                        onClick = {
                            soundManager.toggleSound()
                        }
                    )

                    GlassIconButton(
                        icon = Icons.Default.History,
                        contentDescription = "سجل الطلبيات",
                        onClick = {
                            soundManager.playClick()
                            onOpenOrdersScreen()
                        }
                    )
                }

                // Right Controls: AI Assistant, Offers Bell, Cart
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // AI Pharmacist Button
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                soundManager.playClick()
                                onOpenAiChat()
                            },
                        color = EmeraldPrimary.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "المساعد الذكي",
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "الصيدلاني الذكي",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Offers Bell
                    BadgedBox(
                        badge = {
                            if (offers.any { it.isNew }) {
                                Badge(
                                    containerColor = GoldAccent,
                                    contentColor = EmeraldDark
                                ) {
                                    Text(
                                        text = "${offers.count { it.isNew }}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        GlassIconButton(
                            icon = Icons.Default.Notifications,
                            contentDescription = "العروض والتنبيهات",
                            onClick = {
                                soundManager.playClick()
                                showOffersDialog = true
                            }
                        )
                    }

                    // Cart Button
                    BadgedBox(
                        badge = {
                            if (cartTotalCount > 0) {
                                Badge(
                                    containerColor = GoldAccent,
                                    contentColor = EmeraldDark
                                ) {
                                    Text(
                                        text = "$cartTotalCount",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        GlassIconButton(
                            icon = Icons.Default.ShoppingCart,
                            contentDescription = "السلة",
                            onClick = {
                                soundManager.playClick()
                                onOpenCartScreen()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.15f))

            // Center Logo in Frosted Glass Soft Circle
            Box(
                modifier = Modifier
                    .scale(logoScale)
                    .size(125.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = CircleShape,
                        spotColor = GoldAccent.copy(alpha = 0.45f)
                    )
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.95f))
                    .border(
                        width = 2.5.dp,
                        brush = Brush.linearGradient(listOf(GoldAccent, EmeraldPrimary, GoldAccent)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_logo_alfawaz_1786650981951),
                    contentDescription = "شعار مستودع الفواز",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Brand Title & Slogan
            Text(
                text = "مستودع الفواز للأدوية البشرية",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .width(24.dp)
                        .background(GoldAccent.copy(alpha = 0.7f))
                )
                Text(
                    text = "حيث تبدأ الثقة • Al-Fawaz Pharma",
                    fontSize = 13.sp,
                    color = GoldAccent,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .width(24.dp)
                        .background(GoldAccent.copy(alpha = 0.7f))
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Stats Highlight Chips
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickStatItem("34 وكالة حصرية")
                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(GoldAccent))
                QuickStatItem("300+ صنف دوائي")
                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(GoldAccent))
                QuickStatItem("توصيل سريع")
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Call To Action Button: "تصفح الأدوية"
            Button(
                onClick = {
                    soundManager.playClick()
                    onBrowseDrugs()
                },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(54.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = EmeraldPrimary
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.6f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "تصفح الأدوية",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowBack, // In RTL, ArrowBack points forward (left)
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Quick Action Button: "الشركات والوكالات"
            OutlinedButton(
                onClick = {
                    soundManager.playClick()
                    onOpenCompanies()
                },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(46.dp),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.35f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White.copy(alpha = 0.05f)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "دليل الوكالات والشركات الحصرية",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.35f))

            // Bottom Area: Quiet, Minimalist Social & Contact Icons in the bottom right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 85.dp), // space for floating bottom bar
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: JSON / Database updates shortcut
                TextButton(
                    onClick = {
                        soundManager.playClick()
                        onOpenJsonUpdate()
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "تحديث البيانات",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                // Right: Quiet, subtle, minimalist contact icons (WhatsApp, Call, About)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuietSocialIcon(
                        icon = Icons.Default.Phone,
                        contentDescription = "اتصال مباشر",
                        onClick = {
                            soundManager.playClick()
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0933000000"))
                            context.startActivity(intent)
                        }
                    )

                    QuietSocialIcon(
                        icon = Icons.Default.Send,
                        contentDescription = "واتساب المستودع",
                        onClick = {
                            soundManager.playClick()
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/+963933000000"))
                            context.startActivity(intent)
                        }
                    )

                    QuietSocialIcon(
                        icon = Icons.Default.Info,
                        contentDescription = "عن المستودع",
                        onClick = {
                            soundManager.playClick()
                            showAboutDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showAboutDialog) {
        AboutWarehouseDialog(
            onDismiss = { showAboutDialog = false }
        )
    }

    if (showOffersDialog) {
        OffersDialog(
            offers = offers,
            onDismiss = { showOffersDialog = false },
            onSelectAgency = { agency ->
                onSelectAgencyOffer(agency)
            }
        )
    }
}

@Composable
private fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.12f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(19.dp)
        )
    }
}

@Composable
private fun QuietSocialIcon(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.09f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun QuickStatItem(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = Color.White.copy(alpha = 0.9f)
    )
}
