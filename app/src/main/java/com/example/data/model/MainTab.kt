package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainTab(
    val titleAr: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("الرئيسية", Icons.Filled.Home, Icons.Outlined.Home),
    COMPANIES("الشركات", Icons.Filled.Business, Icons.Outlined.Business),
    SEARCH("الأدوية", Icons.Filled.Search, Icons.Outlined.Search),
    FAVORITES("المفضلة", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
}
