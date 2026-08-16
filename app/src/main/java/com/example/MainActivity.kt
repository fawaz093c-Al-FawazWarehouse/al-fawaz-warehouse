package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.AiChatDialog
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CeliaMilkScreen
import com.example.ui.screens.DrugDetailDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JsonUpdateScreen
import com.example.ui.screens.MasoudSerumScreen
import com.example.ui.screens.MedicineDetailScreen
import com.example.ui.screens.OrderHistoryScreen
import com.example.ui.theme.AlFawazTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.util.LocalSoundManager
import com.example.util.rememberAppSoundManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: MainViewModel = viewModel()
            val isDarkModeOverride by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val systemIsDark = isSystemInDarkTheme()
            val isDark = isDarkModeOverride ?: systemIsDark

            val soundManager = rememberAppSoundManager()

            AlFawazTheme(darkTheme = isDark) {
                val navController = rememberNavController()

                val filteredDrugs by viewModel.filteredDrugs.collectAsStateWithLifecycle()
                val favoriteDrugs by viewModel.favoriteDrugs.collectAsStateWithLifecycle()
                val favoriteIds by viewModel.favoriteDrugIds.collectAsStateWithLifecycle()
                val selectedAgency by viewModel.selectedAgency.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val inStockOnly by viewModel.inStockOnly.collectAsStateWithLifecycle()
                val sortBy by viewModel.sortBy.collectAsStateWithLifecycle()
                val offers by viewModel.offers.collectAsStateWithLifecycle()
                val selectedDrug by viewModel.selectedDrug.collectAsStateWithLifecycle()
                val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
                val cartTotalNet by viewModel.cartTotalNet.collectAsStateWithLifecycle()
                val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
                val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
                val jsonImportState by viewModel.jsonImportState.collectAsStateWithLifecycle()

                var showAiChat by remember { mutableStateOf(false) }

                CompositionLocalProvider(
                    LocalLayoutDirection provides LayoutDirection.Rtl,
                    LocalSoundManager provides soundManager
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("home") {
                            HomeScreen(
                                drugs = filteredDrugs,
                                favoriteDrugs = favoriteDrugs,
                                selectedAgencyId = selectedAgency,
                                searchQuery = searchQuery,
                                inStockOnly = inStockOnly,
                                sortBy = sortBy,
                                favoriteIds = favoriteIds,
                                cartItems = cartItems,
                                offers = offers,
                                isDarkModeOverride = isDarkModeOverride,
                                toastEvent = viewModel.toastEvent,
                                onAgencySelected = { agency -> viewModel.selectAgency(agency) },
                                onSearchQueryChange = { query -> viewModel.setSearchQuery(query) },
                                onInStockToggle = { inStock -> viewModel.setInStockOnly(inStock) },
                                onSortChange = { sort -> viewModel.setSortBy(sort) },
                                onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                                onAddToCart = { drug, qty -> viewModel.addToCart(drug, this@MainActivity, qty) },
                                onOpenDrugDetails = { drug -> navController.navigate("medicine_detail/${drug.id}") },
                                onOpenAiChat = { showAiChat = true },
                                onOpenJsonUpdate = { navController.navigate("json_update") },
                                onOpenCeliaScreen = { navController.navigate("celia") },
                                onOpenMasoudScreen = { navController.navigate("masoud") },
                                onOpenCartScreen = { navController.navigate("cart") },
                                onOpenOrdersScreen = { navController.navigate("orders") },
                                onToggleDarkMode = { sysDark -> viewModel.toggleDarkMode(sysDark) }
                            )
                        }

                        composable(
                            route = "medicine_detail/{drugId}",
                            arguments = listOf(navArgument("drugId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val drugId = backStackEntry.arguments?.getString("drugId") ?: ""
                            val drug = viewModel.getDrugById(drugId)
                            MedicineDetailScreen(
                                drugId = drugId,
                                drug = drug,
                                cartCount = cartItems.sumOf { it.quantity },
                                onBackClick = { navController.popBackStack() },
                                onCartClick = { navController.navigate("cart") },
                                onAddToCart = { d, qty -> viewModel.addToCart(d, this@MainActivity, qty) }
                            )
                        }

                        composable("orders") {
                            OrderHistoryScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToHome = {
                                    navController.popBackStack("home", false)
                                }
                            )
                        }

                        composable("celia") {
                            CeliaMilkScreen(
                                onBackClick = { navController.popBackStack() },
                                onAddToCart = { drug -> viewModel.addToCart(drug, this@MainActivity, 1) },
                                onOpenDrugDetails = { drug -> navController.navigate("medicine_detail/${drug.id}") }
                            )
                        }

                        composable("masoud") {
                            MasoudSerumScreen(
                                onBackClick = { navController.popBackStack() },
                                onAddToCart = { drug, qty -> viewModel.addToCart(drug, this@MainActivity, qty) },
                                onOpenDrugDetails = { drug -> navController.navigate("medicine_detail/${drug.id}") }
                            )
                        }

                        composable("json_update") {
                            JsonUpdateScreen(
                                importState = jsonImportState,
                                onImportJson = { json -> viewModel.importJson(json) },
                                onBackClick = { navController.popBackStack() },
                                onResetState = { viewModel.resetJsonImportState() }
                            )
                        }

                        composable("cart") {
                            CartScreen(
                                cartItems = cartItems,
                                totalNetPrice = cartTotalNet,
                                onUpdateQuantity = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                                onRemoveItem = { id -> viewModel.removeFromCart(id) },
                                onClearCart = { viewModel.clearCart() },
                                onBackClick = { navController.popBackStack() },
                                onSaveOrderToHistory = { pharmacy, notes ->
                                    viewModel.saveCurrentCartToOrderHistory(pharmacy, notes)
                                }
                            )
                        }
                    }

                    // Drug Detail Modal Dialog
                    selectedDrug?.let { drug ->
                        DrugDetailDialog(
                            drug = drug,
                            onDismiss = { viewModel.closeDrugDetails() },
                            onAddToCart = { d -> viewModel.addToCart(d, this@MainActivity, 1) }
                        )
                    }

                    // Gemini AI Chat Modal Dialog
                    if (showAiChat) {
                        AiChatDialog(
                            messages = chatMessages,
                            isLoading = isAiLoading,
                            onSendMessage = { text, bitmap -> viewModel.sendAiMessage(text, bitmap) },
                            onDismiss = { showAiChat = false }
                        )
                    }
                }
            }
        }
    }
}
