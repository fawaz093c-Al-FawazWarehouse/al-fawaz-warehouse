package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Drug
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SoftGreen
import com.example.ui.theme.SoftRed
import com.example.ui.theme.WarmAmber

@Composable
fun DrugCard(
    drug: Drug,
    onAddToCart: (Drug, Int) -> Unit,
    onOpenDetails: (Drug) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onOpenDetails(drug) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Row: Agency Badge & Availability Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = drug.agencyName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Status Badge ("متوفر" / "غير متوفر")
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (drug.isAvailable) SoftGreen.copy(alpha = 0.12f) else SoftRed.copy(alpha = 0.12f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (drug.isAvailable) SoftGreen else SoftRed)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (drug.isAvailable) "متوفر" else "غير متوفر",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (drug.isAvailable) SoftGreen else SoftRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Drug Title & Scientific Name
            Text(
                text = drug.tradeName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (drug.scientificName.isNotBlank()) {
                Text(
                    text = drug.scientificName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Price Details Grid (Public, Pharmacist, Net)
            val formatPrice: (Double) -> String = { price ->
                if (drug.agencyId == "celia" || drug.agencyId == "masoud_serum") {
                    "$${String.format("%.2f", price)}"
                } else {
                    "${String.format("%,d", price.toLong())} ل.س"
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriceItem("سعر العموم", formatPrice(drug.publicPrice))
                Divider(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                PriceItem("سعر الصيدلي", formatPrice(drug.pharmacistPrice))
                Divider(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                PriceItem("الصافي (Net)", formatPrice(drug.netPrice), isHighlight = true)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Action Row: Bonus ratio, Net code, & Add to Cart button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (drug.bonusRatio.isNotBlank()) {
                        Text(
                            text = "العرض/البونص: ${drug.bonusRatio}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarmAmber
                        )
                    }
                    Text(
                        text = "كود الصافي: ${drug.netCode}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { onAddToCart(drug, 1) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (drug.isAvailable) EmeraldPrimary else SoftRed.copy(alpha = 0.8f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (drug.isAvailable) Icons.Default.AddShoppingCart else Icons.Default.Warning,
                        contentDescription = "شراء",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (drug.isAvailable) "إضافة للطلب" else "غير متوفر (شراء)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceItem(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isHighlight) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (isHighlight) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}
