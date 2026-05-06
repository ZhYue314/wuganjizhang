package com.seamless.bookkeeper.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Home : BottomNavItem("home", "\u9996\u9875", Icons.Default.Home, Icons.Default.Home)
    object Stats : BottomNavItem("stats", "\u7edf\u8ba1", Icons.Default.BarChart, Icons.Default.BarChart)
    object Calendar : BottomNavItem("calendar", "\u65e5\u5386", Icons.Default.CalendarMonth, Icons.Default.CalendarMonth)
}
