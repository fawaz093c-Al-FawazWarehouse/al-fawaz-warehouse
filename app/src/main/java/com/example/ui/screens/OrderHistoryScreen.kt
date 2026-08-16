package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.data.local.entity.OrderEntity
import com.example.data.repository.OrderRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val context = LocalContext.current
    var selectedFilterStatus by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }
    var orderToDelete by remember { mutableStateOf<OrderEntity?>(null) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    val statusFilters = listOf("الكل", "قيد المعالجة", "تم التجهيز", "تم الشحن", "مكتملة", "ملغاة")

    val filteredOrders = remember(orders, selectedFilterStatus, searchQuery) {
        orders.filter { order ->
            val matchesStatus = selectedFilterStatus == "الكل" || order.status == selectedFilterStatus
            val q = searchQuery.trim()
            val matchesQuery = q.isEmpty() ||
                    order.orderNumber.contains(q, ignoreCase = true) ||
                    order.pharmacyName.contains(q, ignoreCase = true) ||
                    order.notes.contains(q, ignoreCase = true) ||
                    order.itemsJson.contains(q, ignoreCase = true)

            matchesStatus && matchesQuery
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "سجل الطلبيات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${orders.size} طلبية مسجلة محلياً",
                            fontSize = 12.sp,
                            color = GoldAccent
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (orders.isNotEmpty()) {
                        IconButton(onClick = { showClearAllDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "مسح كل السجل",
                                tint = Color(0xFFFF8A80)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EmeraldDark
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search & Filter Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("ابحث برقم الطلب أو اسم الصيدلية أو الصنف...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = EmeraldPrimary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "مسح",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        statusFilters.forEach { status ->
                            val isSelected = selectedFilterStatus == status
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilterStatus = status },
                                label = {
                                    Text(
                                        text = status,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }

            if (filteredOrders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (searchQuery.isNotEmpty() || selectedFilterStatus != "الكل")
                                "لا توجد نتائج مطابقة لخيارات البحث"
                            else
                                "لا توجد طلبيات سابقة مسجلة بعد",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "الطلبيات التي تؤكد إرسالها عبر الواتساب ستُحفظ تلقائياً هنا في قاعدة بيانات Room لمتابعتها وإعادة طلبها لاحقاً.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onNavigateToHome,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.LocalPharmacy, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تصفح الأدوية والطلب الآن", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders, key = { it.id }) { order ->
                        OrderCardItem(
                            order = order,
                            onStatusChange = { newStatus ->
                                viewModel.updateOrderStatus(order.id, newStatus)
                            },
                            onDelete = {
                                orderToDelete = order
                            },
                            onReorder = {
                                viewModel.reorderFromHistory(order, context)
                            },
                            onShareWhatsApp = {
                                shareOrderToWhatsApp(context, order)
                            }
                        )
                    }
                }
            }
        }
    }

    // Confirmation dialog for deleting single order
    orderToDelete?.let { order ->
        AlertDialog(
            onDismissRequest = { orderToDelete = null },
            title = { Text("حذف الطلبية", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من رغبتك بحذف الطلبية رقم ${order.orderNumber} نهائياً؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteOrder(order.id)
                        orderToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Confirmation dialog for clearing all history
    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text("مسح كل سجل الطلبيات", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من حذف جميع الطلبيات المسجلة من قاعدة البيانات؟ لا يمكن التراجع عن هذا الإجراء.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllOrders()
                        showClearAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("مسح الكل")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCardItem(
    order: OrderEntity,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit,
    onReorder: () -> Unit,
    onShareWhatsApp: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }
    val sypFormatter = remember { NumberFormat.getNumberInstance(Locale.US) }
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar")) }
    val formattedDate = remember(order.timestamp) { dateFormat.format(Date(order.timestamp)) }
    val items = remember(order.itemsJson) { OrderRepository.parseItemsJson(order.itemsJson) }

    val statusColor = when (order.status) {
        "قيد المعالجة" -> Color(0xFFE65100)
        "تم التجهيز" -> Color(0xFF0277BD)
        "تم الشحن" -> Color(0xFF6A1B9A)
        "مكتملة" -> EmeraldPrimary
        "ملغاة" -> Color(0xFFC62828)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val statusBg = when (order.status) {
        "قيد المعالجة" -> Color(0xFFFFF3E0)
        "تم التجهيز" -> Color(0xFFE1F5FE)
        "تم الشحن" -> Color(0xFFF3E5F5)
        "مكتملة" -> EmeraldPrimary.copy(alpha = 0.15f)
        "ملغاة" -> Color(0xFFFFEBEE)
        else -> Color(0xFFEEEEEE)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Order Number, Date, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "طلبية: ${order.orderNumber}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = formattedDate,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Status Badge with Dropdown
                Box {
                    Surface(
                        color = statusBg,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { showStatusMenu = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = order.status,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        listOf("قيد المعالجة", "تم التجهيز", "تم الشحن", "مكتملة", "ملغاة").forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st, fontWeight = if (st == order.status) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    onStatusChange(st)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pharmacy Name and Summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalPharmacy,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = order.pharmacyName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "${order.itemsCount} عبوة / قطعة",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Totals Row (SYP & USD)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (order.totalSYP > 0) {
                        Text(
                            text = "المجموع: ${sypFormatter.format(order.totalSYP.toLong())} ل.س",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                    if (order.totalUSD > 0) {
                        Text(
                            text = "مجموع سليليا ومسعود: \$${String.format(Locale.US, "%.2f", order.totalUSD)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                    }
                }

                TextButton(
                    onClick = { isExpanded = !isExpanded }
                ) {
                    Text(
                        text = if (isExpanded) "إخفاء التفاصيل" else "عرض الأصناف (${items.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Expanded Items List
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(8.dp))

                    if (order.notes.isNotBlank()) {
                        Surface(
                            color = Color(0xFFFFFDE7),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notes,
                                    contentDescription = null,
                                    tint = Color(0xFFF57F17),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ملاحظات: ${order.notes}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }
                    }

                    items.forEachIndexed { idx, item ->
                        val isUsd = item.agencyId == "celia" || item.agencyId == "masoud_serum"
                        val priceText = if (isUsd) {
                            "\$${String.format(Locale.US, "%.2f", item.netPrice)}"
                        } else {
                            "${sypFormatter.format(item.netPrice.toLong())} ل.س"
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${idx + 1}.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = item.tradeName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${item.agencyName} ${if (item.bonusRatio.isNotBlank()) "• بونص: ${item.bonusRatio}" else ""}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "الكمية: ${item.quantity}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                                Text(
                                    text = priceText,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (idx < items.size - 1) {
                            HorizontalDivider(
                                color = Color.LightGray.copy(alpha = 0.2f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons: Reorder, Share WhatsApp, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "حذف الطلبية",
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // WhatsApp Share
                    OutlinedButton(
                        onClick = onShareWhatsApp,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "مشاركة",
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Reorder
                    Button(
                        onClick = onReorder,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "إعادة الطلب",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun shareOrderToWhatsApp(context: Context, order: OrderEntity) {
    val items = OrderRepository.parseItemsJson(order.itemsJson)
    val sypFormatter = NumberFormat.getNumberInstance(Locale.US)

    val msg = buildString {
        appendLine("📦 *طلبية سابقة - مستودع الفواز للأدوية*")
        appendLine("🏷️ *رقم الطلب:* ${order.orderNumber}")
        appendLine("🏥 *الصيدلية:* ${order.pharmacyName}")
        appendLine("📊 *الحالة:* ${order.status}")
        appendLine("------------------------------")
        items.forEachIndexed { i, item ->
            val isUsd = item.agencyId == "celia" || item.agencyId == "masoud_serum"
            val priceStr = if (isUsd) "\$${String.format(Locale.US, "%.2f", item.netPrice)}" else "${sypFormatter.format(item.netPrice.toLong())} ل.س"
            appendLine("${i + 1}. *${item.tradeName}* (${item.agencyName})")
            appendLine("   الكمية: ${item.quantity} | السعر: $priceStr ${if (item.bonusRatio.isNotBlank()) "| بونص: ${item.bonusRatio}" else ""}")
        }
        appendLine("------------------------------")
        if (order.totalSYP > 0) {
            appendLine("💰 *المجموع (ل.س):* ${sypFormatter.format(order.totalSYP.toLong())} ل.س")
        }
        if (order.totalUSD > 0) {
            appendLine("💵 *مجموع سليليا ومسعود (بالدولار):* \$${String.format(Locale.US, "%.2f", order.totalUSD)}")
        }
        if (order.notes.isNotBlank()) {
            appendLine("📝 *ملاحظات:* ${order.notes}")
        }
    }

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(msg)}")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, msg)
        }
        context.startActivity(Intent.createChooser(shareIntent, "مشاركة تفاصيل الطلبية"))
    }
}

