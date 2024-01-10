package io.github.mrgsrylm.skso

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.mrgsrylm.skso.ui.component.OfflineDialog
import io.github.mrgsrylm.skso.ui.navigation.AppBottomNavBar
import io.github.mrgsrylm.skso.ui.navigation.AppNavHost
import io.github.mrgsrylm.skso.ui.navigation.AppSplash
import io.github.mrgsrylm.skso.ui.state.AppState
import io.github.mrgsrylm.skso.ui.state.rememberAppState
import io.github.mrgsrylm.skso.ui.theme.SKSOTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SKSOTheme {
                App(badgeCount = 2) {
                }
            }
        }
    }
}

@Composable
fun App(
    modifier: Modifier = Modifier,
    appState: AppState = rememberAppState(),
    badgeCount: Int,
    onBadgeCountChange: (Int) -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!appState.onlineStatus) {
            OfflineDialog(onRetry = appState::refreshOnline)
        } else {
            val navController = rememberNavController()
            val bottomBarState = rememberSaveable { (mutableStateOf(false)) }
            val navBackStackEntry by navController.currentBackStackEntryAsState()

            when (navBackStackEntry?.destination?.route) {
                AppSplash.route -> bottomBarState.value = false
                else -> bottomBarState.value = true
            }

            Scaffold(
                bottomBar = {
                    if (bottomBarState.value) {
                        AppBottomNavBar(
                            navController = navController,
                            bottomBarState = bottomBarState,
                            badgeState = badgeCount,
                        )
                    }
                },
            ) { paddingValues ->
                AppNavHost(
                    navController = navController,
                    modifier = Modifier.padding(paddingValues),
                    onBadgeCountChange = onBadgeCountChange,
                )
            }
        }
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED