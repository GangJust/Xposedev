package io.github.xposedev.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.xposedev.ui.item.NavItem
import io.github.xposedev.ui.page.HomePage
import io.github.xposedev.ui.page.ScopeListPage
import io.github.xposedev.ui.theme.XposeDevTheme

object Navigation {
    const val Home = "Home"
    const val ScopeList = "ScopeList"
}

val navigationItems = listOf(
    NavItem(0, Icons.Rounded.Home, "首页", Navigation.Home),
    NavItem(1, Icons.AutoMirrored.Rounded.List, "作用域", Navigation.ScopeList),
)

val LocalSnackbarHostState = compositionLocalOf(structuralEqualityPolicy()) { SnackbarHostState() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XposeDevApp() {
    XposeDevTheme {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val snackbarHostState = LocalSnackbarHostState.current

        CompositionLocalProvider(
            value = LocalSnackbarHostState provides snackbarHostState,
        ) {
            Scaffold(
                topBar = {
                    TopBarView(
                        scrollBehavior = scrollBehavior,
                    )
                },
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                    )
                },
                bottomBar = {
                    BottomBarView(
                        selectedRoute = backStackEntry?.destination?.route ?: Navigation.Home,
                        navController = navController,
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Navigation.Home,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    composable(Navigation.Home) {
                        HomePage()
                    }

                    composable(Navigation.ScopeList) {
                        ScopeListPage()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarView(
    scrollBehavior: TopAppBarScrollBehavior,
) {
    MediumTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = "XposeDev",
            )
        },
    )
}

@Composable
private fun BottomBarView(
    selectedRoute: String,
    navController: NavHostController,
) {
    NavigationBar {
        navigationItems.forEach { item ->
            NavigationBarItem(
                selected = selectedRoute == item.route,
                alwaysShowLabel = false,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                    )
                }
            )
        }
    }
}