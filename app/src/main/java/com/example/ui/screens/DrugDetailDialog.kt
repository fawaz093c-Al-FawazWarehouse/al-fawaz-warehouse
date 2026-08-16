package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Drug
import com.example.data.model.getAgencyLogo
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SoftGreen
import com.example.ui.theme.SoftRed
import com.example.ui.theme.WarmAmber
import com.example.util.LocalSoundManager

@Composable
fun DrugDetailDialog(
    drug: Drug,
    onDismiss: () -> Unit,
    onAddToCart: (Drug) -> Unit
) {
    val soundManager = LocalSoundManager.current
    val agencyLogoRes = getAgencyLogo(drug.agencyName)

    Dialog(onDismissRequest = {
        soundManager.playDismiss()
        onDismiss()
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalPharmacy,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "تفاصيل الدواء",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = {
                        soundManager.playDismiss()
                        onDismiss()
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Trade Name
                Text(
                    text = drug.tradeName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EmeraldDark
                )

                if (drug.scientificName.isNotBlank()) {
                    Text(
                        text = "الاسم العلمي: ${drug.scientificName}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Agency & Availability Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { },
                        leadingIcon = if (agencyLogoRes != null) {
                            {
                                Image(
                                    painter = painterResource(id = agencyLogoRes),
                                    contentDescription = drug.agencyName,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        } else null,
                        label = { Text("وكالة: ${drug.agencyName}", fontSize = 11.sp, color = EmeraldPrimary) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = EmeraldPrimary.copy(alpha = 0.1f))
                    )

                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = if (drug.isAvailable) "متوفر للمستودع" else "غير متوفر حالياً",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (drug.isAvailable) SoftGreen else SoftRed
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (drug.isAvailable) SoftGreen.copy(alpha = 0.1f) else SoftRed.copy(alpha = 0.1f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description / Composition / Dosage
                if (drug.description.isNotBlank()) {
                    DetailBlock("وصف المستحضر:", drug.description)
                }
                if (drug.composition.isNotBlank()) {
                    DetailBlock("التركيب العلمي:", drug.composition)
                }
                if (drug.dosage.isNotBlank()) {
                    DetailBlock("طريقة الاستخدام:", drug.dosage)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Price Summary Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("سعر العموم:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$${String.format("%.2f", drug.publicPrice)} USD", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("سعر الصيدلي:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$${String.format("%.2f", drug.pharmacistPrice)} USD", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Divider(modifier = Modifier.padding(vertical = 6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("الصافي (Net Price):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                            Text("$${String.format("%.2f", drug.netPrice)} USD", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldPrimary)
                        }

                        if (drug.bonusRatio.isNotBlank()) {
                            Text("البونص/العرض: ${drug.bonusRatio}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WarmAmber)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Add to Cart Button
                Button(
                    onClick = {
                        soundManager.playAddToCart()
                        onAddToCart(drug)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (drug.isAvailable) EmeraldPrimary else SoftRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = if (drug.isAvailable) Icons.Default.AddShoppingCart else Icons.Default.Warning,
                        contentDescription = "طلب",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (drug.isAvailable) "إضافة إلى سلة الطلب" else "طلب الدواء (غير متوفر)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailBlock(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
        Text(text = value, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

