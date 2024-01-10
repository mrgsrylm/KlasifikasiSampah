package io.github.mrgsrylm.skso.ui.navigation

import io.github.mrgsrylm.skso.R

sealed class AppNavItem(
    val title: String,
    val icon: Int,
    val desc: Int,
    val route: String,
) {
    object Home : AppNavItem(
        title = "Home",
        icon = R.drawable.baseline_apps_24,
        desc = R.string.home,
        route = AppHome.route,
    )

    object Log : AppNavItem(
        title = "Log",
        icon = R.drawable.baseline_list_alt_24,
        desc = R.string.log,
        route = AppLog.route,
    )

    object Notification : AppNavItem(
        title = "Notification",
        icon = R.drawable.baseline_notifications_24,
        desc = R.string.notification,
        route = AppNotification.route,
    )
}