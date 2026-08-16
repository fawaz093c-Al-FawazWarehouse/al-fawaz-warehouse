package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.data.model.Drug
import com.example.data.model.MainTab
import com.example.data.model.SortOption
import com.example.data.model.WarehouseOffer
import com.example.ui.components.FloatingBottomBar
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.util.LocalSoundManager
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun HomeScreen(
    drugs: List<Drug>,
    favoriteDrugs: List<Drug>,
    selectedAgencyId: String?,
    searchQuery: String,
    inStockOnly: Boolean,
    sortBy: SortOption,
    favoriteIds: Set<String>,
    cartItems: List<CartItem>,
    offers: List<WarehouseOffer>,
    isDarkModeOverride: Boolean?,
    toastEvent: SharedFlow<String>,
    onAgencySelected: (String?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onInStockToggle: (Boolean) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddToCart: (Drug, Int) -> Unit,
    onOpenDrugDetails: (Drug) -> Unit,
    onOpenAiChat: () -> Unit,
    onOpenJsonUpdate: () -> Unit,
    onOpenCeliaScreen: () -> Unit,
    onOpenMasoudScreen: () -> Unit,
    onOpenCartScreen: () -> Unit,
    onOpenOrdersScreen: () -> Unit,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val soundManager = LocalSoundManager.current

    var currentTab by remember { mutableStateOf(MainTab.HOME) }
    var isBottomBarVisible by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = toastEvent) {
        toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    val cartTotalCount = cartItems.sumOf { it.quantity }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Content with cinematic Fade + Scale transition
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = {
                (fadeIn(animationSpec = tween(320)) + scaleIn(initialScale = 0.96f, animationSpec = tween(320)))
                    .togetherWith(fadeOut(animationSpec = tween(220)) + scaleOut(targetScale = 0.98f, animationSpec = tween(220)))
            },
            label = "tabTransition",
            modifier = Modifier.fillMaxSize()
        ) { tab ->
            when (tab) {
                MainTab.HOME -> {
                    HeroCoverScreen(
                        cartItems = cartItems,
                        offers = offers,
                        isDarkModeOverride = isDarkModeOverride,
                        onBrowseDrugs = {
                            currentTab = MainTab.SEARCH
                        },
                        onOpenCompanies = {
                            currentTab = MainTab.COMPANIES
                        },
                        onOpenAiChat = onOpenAiChat,
                        onOpenJsonUpdate = onOpenJsonUpdate,
                        onOpenOrdersScreen = onOpenOrdersScreen,
                        onOpenCartScreen = onOpenCartScreen,
                        onToggleDarkMode = onToggleDarkMode,
                        onSelectAgencyOffer = { agency ->
                            onAgencySelected(agency)
                            currentTab = MainTab.SEARCH
                        }
                    )
                }

                MainTab.COMPANIES -> {
                    CompaniesScreen(
                        onSelectAgency = { agency ->
                            onAgencySelected(agency)
                            currentTab = MainTab.SEARCH
                        },
                        onOpenCeliaScreen = onOpenCeliaScreen,
                        onOpenMasoudScreen = onOpenMasoudScreen
                    )
                }

                MainTab.SEARCH -> {
                    SearchCatalogScreen(
                        drugs = drugs,
                        selectedAgencyId = selectedAgencyId,
                        searchQuery = searchQuery,
                        inStockOnly = inStockOnly,
                        sortBy = sortBy,
                        favoriteIds = favoriteIds,
                        onAgencySelected = onAgencySelected,
                        onSearchQueryChange = onSearchQueryChange,
                        onInStockToggle = onInStockToggle,
                        onSortChange = onSortChange,
                        onToggleFavorite = onToggleFavorite,
                        onAddToCart = onAddToCart,
                        onOpenDrugDetails = onOpenDrugDetails,
                        onOpenCeliaScreen = onOpenCeliaScreen,
                        onOpenMasoudScreen = onOpenMasoudScreen,
                        onScrollDirectionChanged = { visible ->
                            isBottomBarVisible = visible
                        }
                    )
                }

                MainTab.FAVORITES -> {
                    FavoritesScreen(
                        favoriteDrugs = favoriteDrugs,
                        onToggleFavorite = onToggleFavorite,
                        onAddToCart = onAddToCart,
                        onBrowseCatalog = {
                            currentTab = MainTab.SEARCH
                        }
                    )
                }
            }
        }

        // Floating Cart FAB on Search & Companies & Favorites tab
        if (currentTab != MainTab.HOME) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 86.dp, start = 16.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                BadgedBox(
                    badge = {
                        if (cartTotalCount > 0) {
                            Badge(
                                containerColor = GoldAccent,
                                contentColor = EmeraldDark
                            ) {
                                Text(
                                    text = "$cartTotalCount",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                ) {
                    FloatingActionButton(
                        onClick = {
                            soundManager.playClick()
                            onOpenCartScreen()
                        },
                        containerColor = EmeraldPrimary,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "سلة الطلبية"
                        )
                    }
                }
            }
        }

        // Floating Bottom Bar with 4 tabs
        FloatingBottomBar(
            selectedTab = currentTab,
            onTabSelected = { tab ->
                currentTab = tab
                isBottomBarVisible = true
            },
            favoritesCount = favoriteIds.size,
            isVisible = isBottomBarVisible,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
