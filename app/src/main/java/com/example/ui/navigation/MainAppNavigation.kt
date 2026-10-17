package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CaseDetailsScreen
import com.example.ui.screens.CompletedCasesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyCasesScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PendingCasesScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReportMissingChildScreen
import com.example.ui.screens.SafetyHelplinesScreen
import com.example.ui.screens.SubmitSightingScreen
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TrustBlue
import com.example.ui.viewmodel.MainViewModel
import com.example.util.LocalAppLanguage
import com.example.util.stringRes

sealed class Screen(
    val route: String,
    val titleKey: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : Screen("home", "nav_home", Icons.Filled.Home, Icons.Outlined.Home)
    data object Pending : Screen("pending", "nav_pending", Icons.Filled.HourglassTop, Icons.Outlined.HourglassTop)
    data object Completed : Screen("completed", "nav_completed", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle)
    data object MyCases : Screen("my_cases", "nav_my_cases", Icons.Filled.Assignment, Icons.Outlined.Assignment)
    data object Profile : Screen("profile", "nav_profile", Icons.Filled.Person, Icons.Outlined.Person)

    // Sub-screens
    data object Report : Screen("report_missing_child", "report_missing_child", Icons.Filled.Home, Icons.Outlined.Home)
    data object CaseDetails : Screen("case_details/{caseId}", "view_details", Icons.Filled.Home, Icons.Outlined.Home) {
        fun createRoute(caseId: String) = "case_details/$caseId"
    }
    data object SubmitSighting : Screen("submit_sighting/{caseId}", "confidential_sighting", Icons.Filled.Home, Icons.Outlined.Home) {
        fun createRoute(caseId: String) = "submit_sighting/$caseId"
    }
    data object AdminDashboard : Screen("admin_dashboard", "admin_portal", Icons.Filled.Home, Icons.Outlined.Home)
    data object Notifications : Screen("notifications", "notifications", Icons.Filled.Home, Icons.Outlined.Home)
    data object Helplines : Screen("helplines", "helplines", Icons.Filled.Home, Icons.Outlined.Home)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Pending,
    Screen.Completed,
    Screen.MyCases,
    Screen.Profile
)

@Composable
fun MainAppNavigation(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentLang by viewModel.currentLanguage.collectAsState()

    CompositionLocalProvider(
        LocalLayoutDirection provides currentLang.layoutDirection,
        LocalAppLanguage provides currentLang
    ) {
        val showBottomBar = bottomNavItems.any { it.route == currentRoute }

        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    NavigationBar(
                        containerColor = NavyDark,
                        tonalElevation = 8.dp
                    ) {
                        bottomNavItems.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                        contentDescription = stringRes(screen.titleKey)
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringRes(screen.titleKey),
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NavyDark,
                                    selectedTextColor = AmberAccent,
                                    indicatorColor = AmberAccent,
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToReport = { navController.navigate(Screen.Report.route) },
                        onNavigateToDetails = { id -> navController.navigate(Screen.CaseDetails.createRoute(id)) },
                        onNavigateToSighting = { id -> navController.navigate(Screen.SubmitSighting.createRoute(id)) },
                        onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) }
                    )
                }

                composable(Screen.Pending.route) {
                    PendingCasesScreen(
                        viewModel = viewModel,
                        onNavigateToDetails = { id -> navController.navigate(Screen.CaseDetails.createRoute(id)) },
                        onNavigateToSighting = { id -> navController.navigate(Screen.SubmitSighting.createRoute(id)) }
                    )
                }

                composable(Screen.Completed.route) {
                    CompletedCasesScreen(
                        viewModel = viewModel,
                        onNavigateToDetails = { id -> navController.navigate(Screen.CaseDetails.createRoute(id)) }
                    )
                }

                composable(Screen.MyCases.route) {
                    MyCasesScreen(
                        viewModel = viewModel,
                        onNavigateToReport = { navController.navigate(Screen.Report.route) },
                        onNavigateToDetails = { id -> navController.navigate(Screen.CaseDetails.createRoute(id)) }
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        viewModel = viewModel,
                        onNavigateToAdmin = { navController.navigate(Screen.AdminDashboard.route) },
                        onNavigateToHelplines = { navController.navigate(Screen.Helplines.route) }
                    )
                }

                composable(Screen.Report.route) {
                    ReportMissingChildScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onSubmissionComplete = { caseId ->
                            navController.navigate(Screen.MyCases.route) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }

                composable(
                    route = Screen.CaseDetails.route,
                    arguments = listOf(navArgument("caseId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val caseId = backStackEntry.arguments?.getString("caseId") ?: ""
                    CaseDetailsScreen(
                        caseId = caseId,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToSighting = { id -> navController.navigate(Screen.SubmitSighting.createRoute(id)) }
                    )
                }

                composable(
                    route = Screen.SubmitSighting.route,
                    arguments = listOf(navArgument("caseId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val caseId = backStackEntry.arguments?.getString("caseId") ?: ""
                    SubmitSightingScreen(
                        caseId = caseId,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.AdminDashboard.route) {
                    AdminDashboardScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Notifications.route) {
                    NotificationsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToDetails = { id -> navController.navigate(Screen.CaseDetails.createRoute(id)) }
                    )
                }

                composable(Screen.Helplines.route) {
                    SafetyHelplinesScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
