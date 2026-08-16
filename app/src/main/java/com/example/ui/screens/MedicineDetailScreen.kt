package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MedicineDetailInfoProvider
import com.example.data.model.Drug
import com.example.data.model.MedicineDetailInfo
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(
    drugId: String,
    drug: Drug?,
    cartCount: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onAddToCart: (Drug, Int) -> Unit
) {
    val context = LocalContext.current
    var quantity by remember { mutableIntStateOf(1) }

    if (drug == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("تفاصيل الدواء", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldDark)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "لم يتم العثور على الدواء المطلوب",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("العودة للقائمة")
                    }
                }
            }
        }
        return
    }

    val detailInfo: MedicineDetailInfo = remember(drug) {
        MedicineDetailInfoProvider.getDetailInfo(drug)
    }

    fun formatPrice(price: Double): String {
        return if (drug.agencyId == "celia" || drug.agencyId == "masoud_serum") {
            "$${"%.2f".format(price)}"
        } else {
            "${price.toLong().toString().reversed().chunked(3).joinToString(",").reversed()} ل.س"
        }
    }

    fun shareMedicineDetails() {
        val shareText = buildString {
            appendLine("📋 *بطاقة معلومات دوائية - مستودع الفواز للأدوية*")
            appendLine("💊 *الدواء:* ${drug.tradeName}")
            if (drug.scientificName.isNotBlank()) appendLine("🔬 *الاسم العلمي:* ${drug.scientificName}")
            appendLine("🏭 *الشركة المصنعة:* ${drug.company} (${detailInfo.manufacturerOrigin})")
            appendLine("🧪 *المادة الفعالة والعيار:* ${detailInfo.activeIngredients}")
            appendLine("📦 *الشكل الصيدلاني:* ${detailInfo.pharmaceuticalForm}")
            appendLine("🏷️ *التصنيف العلاجي:* ${detailInfo.therapeuticClass}")
            appendLine("-------------------------")
            appendLine("💰 *سعر الصافي للمستودع:* ${formatPrice(drug.netPrice)}")
            appendLine("💵 *سعر الصيدلي:* ${formatPrice(drug.pharmacistPrice)}")
            appendLine("🛒 *سعر العموم:* ${formatPrice(drug.publicPrice)}")
            if (drug.bonusRatio.isNotBlank()) appendLine("🎁 *عرض البونص:* ${drug.bonusRatio}")
            appendLine("-------------------------")
            appendLine("⏱️ *جرعة البالغين:* ${detailInfo.adultDosage}")
            appendLine("👶 *جرعة الأطفال:* ${detailInfo.pediatricDosage}")
            appendLine("📌 *إرشادات الاستعمال:* ${detailInfo.usageInstructions}")
            appendLine("-------------------------")
            appendLine("📞 *المندوب المسؤول:* ${detailInfo.representativeName} (${detailInfo.representativeArea})")
            appendLine("📱 *هاتف التواصل:* ${detailInfo.representativePhone}")
            appendLine("مستودع الفواز - الوكيل الحصري لأفضل المنتجات الطبية والصيدلانية")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "مشاركة تفاصيل الدواء عبر:"))
    }

    fun contactRep(phone: String, isWhatsApp: Boolean) {
        try {
            if (isWhatsApp) {
                val cleanPhone = phone.replace("+", "").replace(" ", "").trim()
                val internationalPhone = if (cleanPhone.startsWith("0")) {
                    "963" + cleanPhone.substring(1)
                } else cleanPhone
                val msg = Uri.encode("مرحباً أستاذ ${detailInfo.representativeName}، استفسار بخصوص دواء ${drug.tradeName} من مستودع الفواز للأدوية.")
                val url = "https://api.whatsapp.com/send?phone=$internationalPhone&text=$msg"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } else {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح التطبيق المطلوب: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = drug.tradeName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                        Text(
                            text = "${drug.company} • ${detailInfo.therapeuticClass}",
                            fontSize = 11.sp,
                            color = GoldAccent,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { shareMedicineDetails() }) {
                        Icon(Icons.Default.Share, contentDescription = "مشاركة", tint = Color.White)
                    }
                    BadgedBox(
                        badge = {
                            if (cartCount > 0) {
                                Badge(containerColor = WarmAmber, contentColor = Color.Black) {
                                    Text(cartCount.toString(), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = onCartClick) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "سلة الطلبيات", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldDark)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Quantity Stepper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "إنقاص", tint = MaterialTheme.colorScheme.onSurface)
                        }

                        Text(
                            text = quantity.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "زيادة", tint = EmeraldPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Add to Cart Button with Net Price Total
                    Button(
                        onClick = { onAddToCart(drug, quantity) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (drug.isAvailable) EmeraldPrimary else SoftRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (drug.isAvailable) Icons.Default.AddShoppingCart else Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (drug.isAvailable) "إضافة للطلب (${formatPrice(drug.netPrice * quantity)})" else "غير متوفر حالياً",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Hero Header Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(EmeraldDark, EmeraldPrimary, EmeraldAccent)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (drug.isAvailable) Color(0xFF1B5E20) else SoftRed,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (drug.isAvailable) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (drug.isAvailable) "متوفر بالمستودع" else "غير متوفر بالمستودع",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Surface(
                                color = GoldAccent.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "كود الصافي: ${drug.netCode}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = drug.tradeName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        if (drug.scientificName.isNotBlank()) {
                            Text(
                                text = drug.scientificName,
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "🏭 ${drug.company}",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "💊 ${detailInfo.pharmaceuticalForm.substringBefore(" (")}",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Commercial Pricing & Bonus Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "الأسعار والعروض التجارية",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            color = EmeraldPrimary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "هامش الربح: ${detailInfo.estimatedProfitMargin}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PriceColumn(title = "سعر العموم", value = formatPrice(drug.publicPrice), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Divider(modifier = Modifier.height(30.dp).width(1.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        PriceColumn(title = "سعر الصيدلي", value = formatPrice(drug.pharmacistPrice), color = MaterialTheme.colorScheme.onSurface)
                        Divider(modifier = Modifier.height(30.dp).width(1.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        PriceColumn(title = "سعر الصافي (Net)", value = formatPrice(drug.netPrice), color = EmeraldPrimary, isBold = true)
                    }

                    if (drug.bonusRatio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(WarmAmber.copy(alpha = 0.15f))
                                .border(1.dp, WarmAmber.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "عرض البونص المعتمد: ${drug.bonusRatio}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 3. Active Ingredients & Pharmaceutical Profile
            DetailSectionCard(
                title = "المواد الفعالة والتركيب الصيدلاني",
                icon = Icons.Default.Science,
                iconTint = Color(0xFF1E88E5)
            ) {
                DetailRow(label = "المادة الفعالة والتركيز:", value = detailInfo.activeIngredients)
                DetailRow(label = "الشكل الصيدلاني:", value = detailInfo.pharmaceuticalForm)
                DetailRow(label = "التصنيف العلاجي:", value = detailInfo.therapeuticClass)
                if (drug.description.isNotBlank()) {
                    DetailRow(label = "الوصف الصيدلاني:", value = drug.description)
                }
            }

            // 4. Dosage Guidelines & Usage Instructions
            DetailSectionCard(
                title = "دليل الجرعات وطريقة الاستخدام",
                icon = Icons.Default.Medication,
                iconTint = Color(0xFFE53935)
            ) {
                DetailRow(label = "جرعة البالغين (Adult):", value = detailInfo.adultDosage, isHighlight = true)
                DetailRow(label = "جرعة الأطفال (Pediatric):", value = detailInfo.pediatricDosage)
                DetailRow(label = "إرشادات وتوقيت التناول:", value = detailInfo.usageInstructions)
            }

            // 5. Manufacturer & Sales Representative Profile
            DetailSectionCard(
                title = "بيانات الشركة المصنعة والمندوب المسؤول",
                icon = Icons.Default.Business,
                iconTint = Color(0xFF8E24AA)
            ) {
                DetailRow(label = "الشركة المصنعة:", value = detailInfo.manufacturerName)
                DetailRow(label = "معايير الجودة والمنشأ:", value = detailInfo.manufacturerOrigin)

                Spacer(modifier = Modifier.height(8.dp))

                // Representative Interactive Card
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "مندوب خط التوزيع: ${detailInfo.representativeName}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = detailInfo.representativeArea,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(
                                    onClick = { contactRep(detailInfo.representativePhone, isWhatsApp = false) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "اتصال", tint = Color.White, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = { contactRep(detailInfo.representativePhone, isWhatsApp = true) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF25D366))
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = "واتساب", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            // 6. Clinical Indications & Precautions
            DetailSectionCard(
                title = "دواعي الاستعمال والتحذيرات الطبية",
                icon = Icons.Default.HealthAndSafety,
                iconTint = Color(0xFF00897B)
            ) {
                Text(
                    text = "دواعي الاستعمال الرئيسية:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                detailInfo.indications.forEach { indication ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "• ", color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = indication,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(label = "موانع الاستخدام:", value = detailInfo.contraindications)

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "تحذيرات هامة:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftRed
                )
                detailInfo.warningsAndPrecautions.forEach { warning ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "⚠️ ", fontSize = 11.sp)
                        Text(
                            text = warning,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // 7. Storage Conditions & Safety
            DetailSectionCard(
                title = "شروط الحفظ والتخزين",
                icon = Icons.Default.Inventory2,
                iconTint = Color(0xFFFB8C00)
            ) {
                DetailRow(label = "الحفظ:", value = detailInfo.storageConditions)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = iconTint.copy(alpha = 0.12f),
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun PriceColumn(
    title: String,
    value: String,
    color: Color,
    isBold: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = if (isBold) 13.sp else 12.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = color
        )
    }
}
