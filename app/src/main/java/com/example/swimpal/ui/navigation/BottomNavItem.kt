package com.example.swimpal.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.swimpal.R

sealed class BottomNavItem(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    object Main : BottomNavItem("main", R.string.main, Icons.Default.Home)
    object Generate : BottomNavItem("generate", R.string.generate, Icons.Default.AddCircle)
    object Training : BottomNavItem("training", R.string.training, Icons.Default.List)
    object Profile : BottomNavItem("profile", R.string.profile, Icons.Default.Person)
}
