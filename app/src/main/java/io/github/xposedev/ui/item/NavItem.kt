package io.github.xposedev.ui.item

import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val index: Int,
    val icon: ImageVector,
    val label: String,
    val route: String,
)