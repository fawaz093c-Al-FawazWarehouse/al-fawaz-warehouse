package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
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
import com.example.data.model.Agency
import com.example.data.model.EXCLUSIVE_AGENCIES
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.util.LocalSoundManager

@Composable
fun AgencyGrid(
    selectedAgencyId: String?,
    onAgencySelected: (String?) -> Unit,
    onOpenCeliaScreen: () -> Unit,
    onOpenMasoudScreen: () -> Unit
) {
    val soundManager = LocalSoundManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الشركات والوكالات الحصرية",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (selectedAgencyId != null) {
                TextButton(
                    onClick = {
                        soundManager.playTabSwitch()
                        onAgencySelected(null)
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "عرض الكل",
                        fontSize = 11.sp,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "All" filter chip
            item {
                AgencyChip(
                    title = "الجميع",
                    logoRes = null,
                    isSelected = selectedAgencyId == null,
                    onClick = {
                        soundManager.playTabSwitch()
                        onAgencySelected(null)
                    }
                )
            }

            // Exclusive Agencies with their attached logos
            items(EXCLUSIVE_AGENCIES) { agency ->
                AgencyChip(
                    title = agency.nameAr,
                    logoRes = agency.logoRes,
                    isSelected = selectedAgencyId == agency.id,
                    onClick = {
                        soundManager.playTabSwitch()
                        if (agency.id == "celia") {
                            onOpenCeliaScreen()
                        } else {
                            onAgencySelected(agency.id)
                        }
                    }
                )
            }

            // Specialized Screen Shortcut for Masoud Serum
            item {
                SpecializedChip(
                    title = "سيروم مسعود",
                    icon = Icons.Default.LocalHospital,
                    onClick = {
                        soundManager.playTabSwitch()
                        onOpenMasoudScreen()
                    }
                )
            }
        }
    }
}

@Composable
private fun AgencyChip(
    title: String,
    logoRes: Int?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (logoRes != null) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = logoRes),
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(1.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = title,
                    tint = if (isSelected) GoldAccent else EmeraldPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SpecializedChip(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(GoldAccent)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = EmeraldDark,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldDark
            )
        }
    }
}

