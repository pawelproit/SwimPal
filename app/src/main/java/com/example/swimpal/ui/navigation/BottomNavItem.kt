package com.example.swimpal.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.swimpal.R

/**
 * Represents a single item in the bottom navigation bar.
 *
 * Each item defines a navigation [route], title resource and icon used
 * in [MainScreenWithBottomNav].
 *
 * @property route Navigation route associated with the item.
 * @property titleRes String resource id for the label.
 * @property icon Icon displayed in the navigation bar.
 */
sealed class BottomNavItem(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    /**
     * Home/main dashboard screen.
     */
    object Main : BottomNavItem("main", R.string.main, Icons.Default.Home)

    /**
     * Screen for generating or creating trainings.
     */
    object Generate : BottomNavItem("generate", R.string.generate, Icons.Default.AddCircle)

    /**
     * Screen listing user's trainings.
     */
    object Training : BottomNavItem("training", R.string.training, Icons.Default.List)

    /**
     * Profile screen with user data, badges and history.
     */
    object Profile : BottomNavItem("profile", R.string.profile, Icons.Default.Person)
}
