package io.github.mrgsrylm.skso.ui.state

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
) = remember(navController, context) {
    AppState(navController, context)
}

class AppState(
    val navController: NavHostController,
    private val context: Context,
) {
    var onlineStatus by mutableStateOf(isOnline())
        private set

    fun refreshOnline() {
        onlineStatus = isOnline()
    }

    fun navigateBack() {
        navController.popBackStack()
    }

    private fun isOnline(): Boolean {
        val connectivityManager = getSystemService(context, ConnectivityManager::class.java)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager?.activeNetwork
            val capabilities = connectivityManager?.getNetworkCapabilities(network) ?: return false

            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            val networkInfo = connectivityManager?.activeNetworkInfo
            networkInfo?.isConnected == true || networkInfo?.isConnectedOrConnecting == true
        }
    }
}