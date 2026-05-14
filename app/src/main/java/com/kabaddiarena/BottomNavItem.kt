package com.kabaddiarena

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector
) {

    object Home : BottomNavItem(
        "Home",
        Icons.Default.Home
    )

    object Analytics : BottomNavItem(
        "Analytics",
        Icons.Default.Analytics
    )

    object History : BottomNavItem(
        "History",
        Icons.Default.History
    )
}