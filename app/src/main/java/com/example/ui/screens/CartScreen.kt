package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SoftRed
import com.example.ui.theme.WarmAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    totalNetPrice: Double,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearCart: () -> Unit,
    onBackClick: () -> Unit,
    onSaveOrderToHistory: ((String, String) -> Unit)? = null
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "سلة الطلبية",
                            tint = GoldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "سلة طلبية الصيدلية",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "مستودع الفواز - حيث تبدأ الثقة",
                                fontSize = 11.sp,
                                color = GoldAccent
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(onClick = onClearCart) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "تفريغ السلة",
                                tint = SoftRed
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldDark)
            )
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "سلة فارغة",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "سلة الطلبية فارغة حالياً",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "تصفح أدوية الوكالات الحصرية (دومنا، بركات، ميديكو، المتحدة، ابن رشد، لاما، هابي كيور، حليب سيليا، سيروم مسعود) وأضفها لطلبيتك.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("العودة لقائمة الأدوية")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemRow(
                            item = item,
                            onUpdateQty = { newQty -> onUpdateQuantity(item.drugId, newQty) },
                            onRemove = { onRemoveItem(item.drugId) }
                        )
                    }
                }

                // Summary & WhatsApp Order Dispatch Footer
                val totalSYP = cartItems
                    .filter { it.agencyId != "celia" && it.agencyId != "masoud_serum" }
                    .sumOf { it.netPrice * it.quantity }
                val totalUSD = cartItems
                    .filter { it.agencyId == "celia" || it.agencyId == "masoud_serum" }
                    .sumOf { it.netPrice * it.quantity }

                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp,
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    var pharmacyName by remember { mutableStateOf("") }
                    var notes by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        OutlinedTextField(
                            value = pharmacyName,
                            onValueChange = { pharmacyName = it },
                            label = { Text("اسم الصيدلية / الطبيب (اختياري)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("ملاحظات الطلبية (اختياري)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(modifier = Modifier.fillMaxWidth()) {
                            if (totalSYP > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "الصافي بالليرة السورية:",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${String.format("%,d", totalSYP.toLong())} ل.س",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = EmeraldPrimary
                                    )
                                }
                            }

                            if (totalUSD > 0) {
                                if (totalSYP > 0) Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "الصافي بالدولار (سيليا/مسعود):",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "$${String.format("%.2f", totalUSD)} USD",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoldAccent
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val orderSummary = buildString {
                                    appendLine("🏬 *طلبية صيدلية جديدة - مستودع الفواز للأدوية*")
                                    appendLine("---------------------------------------")
                                    cartItems.forEachIndexed { idx, item ->
                                        val isUsd = item.agencyId == "celia" || item.agencyId == "masoud_serum"
                                        val priceStr = if (isUsd) "$${String.format("%.2f", item.netPrice)}" else "${item.netPrice.toLong()} ل.س"
                                        appendLine("${idx + 1}. *${item.tradeName}* (${item.agencyName})")
                                        appendLine("   - الكمية: ${item.quantity} | صافي القطعة: $priceStr")
                                        if (item.bonusRatio.isNotBlank()) {
                                            appendLine("   - العرض/البونص: ${item.bonusRatio}")
                                        }
                                        appendLine("   - كود الصافي: ${item.netCode}")
                                    }
                                    appendLine("---------------------------------------")
                                    if (totalSYP > 0) {
                                        appendLine("💰 *إجمالي الصافي (ليرة سورية):* ${String.format("%,d", totalSYP.toLong())} ل.س")
                                    }
                                    if (totalUSD > 0) {
                                        appendLine("💵 *إجمالي الصافي (دولار - سيليا/مسعود):* $${String.format("%.2f", totalUSD)} USD")
                                    }
                                    if (notes.isNotBlank()) {
                                        appendLine("📝 *ملاحظات:* $notes")
                                    }
                                    appendLine("نشكركم على ثقتكم بمستودع الفواز (حيث تبدأ الثقة)")
                                }

                                onSaveOrderToHistory?.invoke(pharmacyName, notes)

                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, orderSummary)
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "إرسال الطلبية لمندوب الفواز عبر:"))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "إرسال", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "إرسال الطلبية لمندوب مستودع الفواز",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onUpdateQty: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(EmeraldPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalPharmacy,
                    contentDescription = item.tradeName,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.tradeName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val isUsd = item.agencyId == "celia" || item.agencyId == "masoud_serum"
                val priceFormatted = if (isUsd) "$${String.format("%.2f", item.netPrice)}" else "${String.format("%,d", item.netPrice.toLong())} ل.س"

                Text(
                    text = "وكالة: ${item.agencyName} | صافي القطعة: $priceFormatted",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (item.bonusRatio.isNotBlank()) {
                    Text(
                        text = "البونص: ${item.bonusRatio}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarmAmber
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Quantity Control
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                IconButton(
                    onClick = { if (item.quantity > 1) onUpdateQty(item.quantity - 1) else onRemove() },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (item.quantity > 1) Icons.Default.Remove else Icons.Default.Delete,
                        contentDescription = "إنقاص",
                        tint = if (item.quantity > 1) MaterialTheme.colorScheme.onSurface else SoftRed,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "${item.quantity}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                IconButton(
                    onClick = { onUpdateQty(item.quantity + 1) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "زيادة",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
