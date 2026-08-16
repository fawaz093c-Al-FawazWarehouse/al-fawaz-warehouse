package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SoftGreen
import com.example.ui.theme.SoftRed
import com.example.ui.viewmodel.JsonImportResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JsonUpdateScreen(
    importState: JsonImportResult?,
    onImportJson: (String) -> Unit,
    onBackClick: () -> Unit,
    onResetState: () -> Unit
) {
    var jsonText by remember { mutableStateOf("") }

    val sampleTemplate = """
[
  {
    "id": "DOM-101",
    "tradeName": "دومنا فيتامين د3 5000",
    "scientificName": "Cholecalciferol 5000 IU",
    "agencyId": "domna",
    "agencyName": "دومنا (Domna)",
    "publicPrice": 8.0,
    "pharmacistPrice": 6.4,
    "netPrice": 5.2,
    "netCode": "NET-DOM-101",
    "bonusRatio": "10 + 2 مجاناً",
    "isAvailable": true,
    "description": "حبوب فيتامين D3 لعلاج النقص وتعزيز صحة العظام والمناعة."
  }
]
    """.trimIndent()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "تحديث البيانات",
                            tint = GoldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "تحديث قائمة الأدوية (JSON)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "مستودع الفواز للأدوية",
                                fontSize = 11.sp,
                                color = GoldAccent
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldDark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Explanatory Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "تعليمات تحديث أدوية الفواز تلقائياً:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• عند لصق ملف JSON جديد، سيتم اعتماد الأدوية المرفقة بالملف فوراً.\n• الأدوية التي لا توجد بالتحديث الجديد سيتم تحويل حالتها تلقائياً إلى 'غير متوفر'.\n• الأدوية غير المتوفرة لن تسمح بالطلب وتصنع صوتاً وتنبيهاً للصيدلي عند الضغط عليها.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Result Alert
            if (importState != null) {
                when (importState) {
                    is JsonImportResult.Success -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SoftGreen.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "نجاح",
                                    tint = SoftGreen
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تم تحديث ${importState.importedCount} دواء بنجاح في قاعدة بيانات مستودع الفواز!",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SoftGreen
                                )
                            }
                        }
                    }
                    is JsonImportResult.Error -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SoftRed.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "خطأ",
                                    tint = SoftRed
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = importState.message,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SoftRed
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // JSON Input TextField
            OutlinedTextField(
                value = jsonText,
                onValueChange = {
                    jsonText = it
                    onResetState()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                label = { Text("الصق نص ملف JSON هنا:") },
                placeholder = { Text(sampleTemplate, fontSize = 11.sp) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        jsonText = sampleTemplate
                        onResetState()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("نموذج تجريبي", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        if (jsonText.isNotBlank()) {
                            onImportJson(jsonText)
                        }
                    },
                    modifier = Modifier.weight(1.2f),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تطبيق التحديث", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
