package io.github.mrgsrylm.skso.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopNavBar(
    navController: NavController
) {
    val currentRoute =
        rememberUpdatedState(navController.currentBackStackEntryAsState().value?.destination?.route)

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(currentRoute.value?.let { getTitleFromRoute(it) } ?: "")
        }
    )
}

private fun getTitleFromRoute(route: String): String {
    return when (route) {
        AppNavItem.Home.route -> "Home Title"
        AppNavItem.Log.route -> "Log Title"
        AppNavItem.Notification.route -> "Notification Title"
        // Add more cases as needed for other routes
        else -> "Default Title"
    }
}