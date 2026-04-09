package com.example.wuganjizhang.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.wuganjizhang.R
import com.example.wuganjizhang.ui.screens.calendar.CalendarScreen
import com.example.wuganjizhang.ui.screens.home.HomeScreen
import com.example.wuganjizhang.ui.screens.stats.StatsScreen

sealed class Screen(val route: String, val title: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", R.string.nav_home, Icons.Default.Home)
    object Stats : Screen("stats", R.string.nav_stats, Icons.Default.Star)
    object Calendar : Screen("calendar", R.string.nav_calendar, Icons.Default.DateRange)
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    
    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Stats.route) {
                StatsScreen()
            }
            composable(Screen.Calendar.route) {
                CalendarScreen()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val screens = listOf(Screen.Home, Screen.Stats, Screen.Calendar)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = stringResource(screen.title)) },
                label = { Text(stringResource(screen.title)) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
