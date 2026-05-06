package com.seamless.bookkeeper.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.seamless.bookkeeper.presentation.screens.account.AccountManagementScreen
import com.seamless.bookkeeper.presentation.screens.calendar.CalendarScreen
import com.seamless.bookkeeper.presentation.screens.category.CategoryManagementScreen
import com.seamless.bookkeeper.presentation.screens.home.HomeScreen
import com.seamless.bookkeeper.presentation.screens.search.SearchScreen
import com.seamless.bookkeeper.presentation.screens.settings.SettingsScreen
import com.seamless.bookkeeper.presentation.screens.transaction.AddTransactionScreen
import com.seamless.bookkeeper.presentation.screens.settings.SettingsViewModel
import com.seamless.bookkeeper.presentation.screens.stats.StatsScreen
import com.seamless.bookkeeper.presentation.theme.BookkeeperTheme
import kotlinx.coroutines.launch

object Routes {
    const val CATEGORY_MANAGEMENT = "category_management"
    const val ACCOUNT_MANAGEMENT = "account_management"
    const val SETTINGS = "settings"
    const val SEARCH = "search"
    const val ADD_TRANSACTION = "add_transaction"
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val settingsViewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val settingsState by settingsViewModel.state.collectAsState()
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentRoute by navController.currentBackStackEntryAsState()
    val route = currentRoute?.destination?.route

    val drawerEnabled = route in listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Stats.route,
        BottomNavItem.Calendar.route
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerEnabled,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                AppDrawerContent(
                    isDarkMode = settingsState.isDarkMode,
                    isAutoMode = settingsState.isAutoMode,
                    onToggleDarkMode = { settingsViewModel.toggleDarkMode(it) },
                    onToggleAutoMode = { settingsViewModel.toggleAutoMode(it) },
                    onNavigate = { dest ->
                        scope.launch { drawerState.close() }
                        navController.navigate(dest) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) {
        BookkeeperTheme(darkTheme = settingsState.isDarkMode) {
            Scaffold(
                modifier = modifier,
                bottomBar = {
                    Box(Modifier.height(64.dp)) {
                        if (drawerEnabled) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                }
            ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onSearchClick = { navController.navigate(Routes.SEARCH) },
                        onAddTransactionClick = { navController.navigate(Routes.ADD_TRANSACTION) }
                    )
                }
                composable(BottomNavItem.Stats.route) {
                    StatsScreen(onMenuClick = { scope.launch { drawerState.open() } })
                }
                composable(BottomNavItem.Calendar.route) {
                    CalendarScreen(onMenuClick = { scope.launch { drawerState.open() } })
                }
                composable(Routes.CATEGORY_MANAGEMENT) {
                    CategoryManagementScreen(onBack = { navController.popBackStack() })
                }
                composable(Routes.ACCOUNT_MANAGEMENT) {
                    AccountManagementScreen(onBack = { navController.popBackStack() })
                }
                composable(Routes.SETTINGS) {
                    SettingsScreen(onBack = { navController.popBackStack() })
                }
                composable(Routes.SEARCH) {
                    SearchScreen(onBack = { navController.popBackStack() })
                }
                composable(
                    route = Routes.ADD_TRANSACTION,
                    enterTransition = { slideInHorizontally(tween(300)) { it } },
                    exitTransition = { slideOutHorizontally(tween(300)) { -it } },
                    popEnterTransition = { slideInHorizontally(tween(300)) { -it } },
                    popExitTransition = { slideOutHorizontally(tween(300)) { it } }
                ) {
                    AddTransactionScreen(onDismiss = { navController.popBackStack() })
                }
            }
        }
    }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(BottomNavItem.Home, BottomNavItem.Stats, BottomNavItem.Calendar)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun AppDrawerContent(
    isDarkMode: Boolean,
    isAutoMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleAutoMode: (Boolean) -> Unit,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(48.dp))
        Text(
            text = "无感记账",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )
        Text(
            text = "完全本地安全存储",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp)
        )
        Spacer(Modifier.height(32.dp))
        HorizontalDivider()

        SectionTitle("常用设置")
        DrawerSettingRow("记录模式", if (isAutoMode) "自动记录" else "确认后记录", onClick = { onToggleAutoMode(!isAutoMode) })
        DrawerSettingRow("深色模式", if (isDarkMode) "深色" else "浅色", onClick = { onToggleDarkMode(!isDarkMode) })
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        SectionTitle("快捷管理")
        DrawerNavItem("分类管理", "🏷", onClick = { onNavigate(Routes.CATEGORY_MANAGEMENT) })
        DrawerNavItem("账户管理", "💳", onClick = { onNavigate(Routes.ACCOUNT_MANAGEMENT) })
        DrawerNavItem("数据备份", "☁", onClick = { })
        DrawerNavItem("权限管理", "🔒", onClick = { })
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        SectionTitle("设置")
        DrawerNavItem("高级设置", "⚙", onClick = { onNavigate(Routes.SETTINGS) })
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        DrawerNavItem("反馈问题", "💬", onClick = { })
        DrawerNavItem("关于", "ℹ", onClick = { })

        Spacer(Modifier.height(16.dp))
        Text(
            "版本 1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
private fun DrawerSettingRow(label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun DrawerNavItem(label: String, icon: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        icon = { Text(icon, fontSize = 20.sp) },
        label = { Text(label) },
        selected = false,
        onClick = onClick
    )
}
