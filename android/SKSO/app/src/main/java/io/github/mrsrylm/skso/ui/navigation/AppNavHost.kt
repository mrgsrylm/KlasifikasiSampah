package io.github.mrsrylm.skso.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.github.mrsrylm.skso.ui.screen.home.HomeScreen
import io.github.mrsrylm.skso.ui.screen.log.LogScreen
import io.github.mrsrylm.skso.ui.screen.notification.NotificationScreen
import io.github.mrsrylm.skso.ui.screen.splash.SplashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onBadgeCountChange: (Int) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AppSplash.route,
        modifier = modifier
    ) {
        composable(AppSplash.route) {
            SplashScreen(
                navigateToHomeScreen = {
                    navController.navigate(AppHome.route)
                }
            )
        }

        composable(AppHome.route) {
            HomeScreen()
        }

        composable(AppLog.route) {
            LogScreen()
        }

        composable(AppNotification.route) {
            NotificationScreen()
        }
    }
}