package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Drug
import com.example.data.model.EXCLUSIVE_AGENCIES
import com.example.data.model.SortOption
import com.example.data.model.getAgencyLogo
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SoftRed
import com.example.util.LocalSoundManager
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SearchCatalogScreen(
    drugs: List<Drug>,
    selectedAgencyId: String?,
    searchQuery: String,
    inStockOnly: Boolean,
    sortBy: SortOption,
    favoriteIds: Set<String>,
    onAgencySelected: (String?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onInStockToggle: (Boolean) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddToCart: (Drug, Int) -> Unit,
    onOpenDrugDetails: (Drug) -> Unit,
    onOpenCeliaScreen: () -> Unit,
    onOpenMasoudScreen: () -> Unit,
    onScrollDirectionChanged: (Boolean) -> Unit = {}
) {
    val soundManager = LocalSoundManager.current
    val listState = rememberLazyListState()

    // Detect scroll to show/hide bottom bar
    var previousIndex by remember { mutableStateOf(0) }
    var previousScrollOffset by remember { mutableStateOf(0) }

    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        val currentIndex = listState.firstVisibleItemIndex
        val currentOffset = listState.firstVisibleItemScrollOffset

        if (currentIndex > previousIndex || (currentIndex == previousIndex && currentOffset > previousScrollOffset + 10)) {
            onScrollDirectionChanged(false) // Scrolling down -> hide
        } else if (currentIndex < previousIndex || (currentIndex == previousIndex && currentOffset < previousScrollOffset - 10)) {
            onScrollDirectionChanged(true) // Scrolling up -> show
        }
        previousIndex = currentIndex
        previousScrollOffset = currentOffset
    }

    var selectedDrugForSheet by remember { mutableStateOf<Drug?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Search Header Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Instant Live Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "ابحث بالاسم التجاري، العلمي، كود الصافي، أو الشركة...",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = EmeraldPrimary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                    )
                )

                // Quick Filters & Sort Bar
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // All Agencies Chip
                    item {
                        FilterChip(
                            selected = selectedAgencyId == null,
                            onClick = {
                                soundManager.playTabSwitch()
                                onAgencySelected(null)
                            },
                            label = { Text("الكل", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    // In-Stock Only Chip
                    item {
                        FilterChip(
                            selected = inStockOnly,
                            onClick = {
                                soundManager.playClick()
                                onInStockToggle(!inStockOnly)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (inStockOnly) Icons.Default.Check else Icons.Default.Inventory2,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            label = { Text("متوفر فقط", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    // Sort By Chip
                    item {
                        Box {
                            AssistChip(
                                onClick = {
                                    soundManager.playClick()
                                    showSortMenu = true
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Sort,
                                        contentDescription = "ترتيب",
                                        tint = if (sortBy != SortOption.DEFAULT) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = "ترتيب: ${sortBy.titleAr}",
                                        fontSize = 11.sp,
                                        fontWeight = if (sortBy != SortOption.DEFAULT) FontWeight.Bold else FontWeight.Normal,
                                        color = if (sortBy != SortOption.DEFAULT) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )

                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                SortOption.values().forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option.titleAr,
                                                fontWeight = if (sortBy == option) FontWeight.Bold else FontWeight.Normal,
                                                color = if (sortBy == option) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            soundManager.playClick()
                                            onSortChange(option)
                                            showSortMenu = false
                                        },
                                        leadingIcon = {
                                            if (sortBy == option) {
                                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Celia Milk Chip Shortcut
                    item {
                        AssistChip(
                            onClick = {
                                soundManager.playClick()
                                onOpenCeliaScreen()
                            },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.ChildCare, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(15.dp))
                            },
                            label = { Text("حليب سيليا", fontSize = 11.sp) }
                        )
                    }

                    // Masoud Serum Shortcut
                    item {
                        AssistChip(
                            onClick = {
                                soundManager.playClick()
                                onOpenMasoudScreen()
                            },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.LocalHospital, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(15.dp))
                            },
                            label = { Text("سيروم مسعود", fontSize = 11.sp) }
                        )
                    }

                    // Specific Agencies list
                    items(EXCLUSIVE_AGENCIES) { agency ->
                        FilterChip(
                            selected = selectedAgencyId == agency.nameAr,
                            onClick = {
                                soundManager.playTabSwitch()
                                onAgencySelected(if (selectedAgencyId == agency.nameAr) null else agency.nameAr)
                            },
                            label = { Text(agency.nameAr, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Count Summary Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "النتائج المتاحة (${drugs.size} صنف)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (selectedAgencyId != null || inStockOnly || sortBy != SortOption.DEFAULT) {
                TextButton(
                    onClick = {
                        soundManager.playTabSwitch()
                        onAgencySelected(null)
                        onInStockToggle(false)
                        onSortChange(SortOption.DEFAULT)
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("إعادة ضبط الفلاتر", fontSize = 11.sp, color = EmeraldPrimary)
                }
            }
        }

        // List of Drugs or Empty/Skeleton State
        if (drugs.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "لم يتم العثور على أدوية مطابقة للبحث أو الفلتر.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 4.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(drugs, key = { it.id }) { drug ->
                    val isFav = favoriteIds.contains(drug.id)

                    SmartDrugRowItem(
                        drug = drug,
                        isFavorite = isFav,
                        onRowClick = {
                            soundManager.playClick()
                            selectedDrugForSheet = drug
                        },
                        onToggleFavorite = {
                            onToggleFavorite(drug.id)
                        },
                        onAddToCart = {
                            onAddToCart(drug, 1)
                        }
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet on Click
    selectedDrugForSheet?.let { drug ->
        DrugDetailBottomSheet(
            drug = drug,
            isFavorite = favoriteIds.contains(drug.id),
            onDismiss = { selectedDrugForSheet = null },
            onAddToCart = { d, qty -> onAddToCart(d, qty) },
            onToggleFavorite = { id -> onToggleFavorite(id) }
        )
    }
}

@Composable
private fun SmartDrugRowItem(
    drug: Drug,
    isFavorite: Boolean,
    onRowClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToCart: () -> Unit
) {
    val soundManager = LocalSoundManager.current
    val agencyLogoRes = getAgencyLogo(drug.agencyName)
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    val heartScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "favScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .shadow(1.5.dp, RoundedCornerShape(14.dp))
            .clickable { onRowClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Left Agency Logo Badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, GoldAccent.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (agencyLogoRes != null) {
                    Image(
                        painter = painterResource(id = agencyLogoRes),
                        contentDescription = drug.agencyName,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = drug.agencyName,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Middle Column: Drug Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = drug.tradeName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!drug.isAvailable) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SoftRed.copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("غير متوفر", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SoftRed)
                        }
                    }
                }

                Text(
                    text = drug.scientificName,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "وكالة: ${drug.agencyName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (drug.bonus.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GoldAccent)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "بونص: ${drug.bonus}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                        }
                    }
                }
            }

            // Right Column: Price + Favorite & Add To Cart Actions
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${numberFormat.format(drug.netPrice)} ل.س",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Favorite Toggle
                    IconButton(
                        onClick = {
                            soundManager.playClick()
                            onToggleFavorite()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "المفضلة",
                            tint = if (isFavorite) SoftRed else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .size(18.dp)
                                .scale(heartScale)
                        )
                    }

                    // Add to Cart
                    IconButton(
                        onClick = {
                            soundManager.playAddToCart()
                            onAddToCart()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (drug.isAvailable) EmeraldPrimary else Color.Gray.copy(alpha = 0.5f)),
                        enabled = drug.isAvailable
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "إضافة للسلة",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
