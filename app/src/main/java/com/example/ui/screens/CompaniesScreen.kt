package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Agency
import com.example.data.model.EXCLUSIVE_AGENCIES
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.util.LocalSoundManager

@Composable
fun CompaniesScreen(
    onSelectAgency: (String) -> Unit,
    onOpenCeliaScreen: () -> Unit,
    onOpenMasoudScreen: () -> Unit
) {
    val soundManager = LocalSoundManager.current
    var companySearch by remember { mutableStateOf("") }

    val filteredAgencies = remember(companySearch) {
        if (companySearch.isBlank()) {
            EXCLUSIVE_AGENCIES
        } else {
            val q = companySearch.trim()
            EXCLUSIVE_AGENCIES.filter {
                it.nameAr.contains(q, ignoreCase = true) ||
                        it.nameEn.contains(q, ignoreCase = true) ||
                        it.description.contains(q, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Header Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "دليل الشركات والوكالات",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "34 وكالة حصرية ومعتمدة لدى مستودع الفواز",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldAccent.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${EXCLUSIVE_AGENCIES.size} وكالة",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                }

                // Company Search Field
                OutlinedTextField(
                    value = companySearch,
                    onValueChange = { companySearch = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("ابحث عن شركة أو وكالة محددة...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = EmeraldPrimary
                        )
                    },
                    trailingIcon = {
                        if (companySearch.isNotEmpty()) {
                            IconButton(onClick = { companySearch = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                    )
                )
            }
        }

        // 2x4 Clean Company Cards Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Special Featured Banners for Celia & Masoud
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SpecialAgencyCard(
                        title = "حليب سيليا للأطفال",
                        subtitle = "كامل الأصناف المعتمدة",
                        badge = "وكالة خاصة",
                        icon = Icons.Default.ChildCare,
                        badgeColor = EmeraldPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            soundManager.playClick()
                            onOpenCeliaScreen()
                        }
                    )

                    SpecialAgencyCard(
                        title = "سيروم مسعود الطبي",
                        subtitle = "سيرومات ومحاليل وريدية",
                        badge = "منتج حصري",
                        icon = Icons.Default.LocalHospital,
                        badgeColor = GoldAccent,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            soundManager.playClick()
                            onOpenMasoudScreen()
                        }
                    )
                }
            }

            // Grid of Agencies
            items(filteredAgencies, key = { it.id }) { agency ->
                CompanyGridCard(
                    agency = agency,
                    onClick = {
                        soundManager.playClick()
                        if (agency.id == "celia") {
                            onOpenCeliaScreen()
                        } else {
                            onSelectAgency(agency.nameAr)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CompanyGridCard(
    agency: Agency,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 8.dp else 2.5.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = EmeraldDark.copy(alpha = 0.15f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Logo Container with clean white circular background and soft border
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .shadow(elevation = 3.dp, shape = CircleShape, spotColor = EmeraldPrimary.copy(alpha = 0.2f))
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, GoldAccent.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (agency.logoRes != null) {
                    Image(
                        painter = painterResource(id = agency.logoRes),
                        contentDescription = agency.nameAr,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = agency.nameAr,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            // Arabic Name
            Text(
                text = agency.nameAr,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // English / Subtitle
            Text(
                text = agency.nameEn,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Action Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = EmeraldPrimary.copy(alpha = 0.1f),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "عرض الأصناف",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowBack, // forward in RTL
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecialAgencyCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(badgeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (badgeColor == GoldAccent) EmeraldPrimary else badgeColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (badgeColor == GoldAccent) EmeraldDark else Color.White
                    )
                }
            }

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
