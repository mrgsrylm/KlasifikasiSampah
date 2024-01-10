package io.github.mrgsrylm.skso.ui.screen.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.mrgsrylm.skso.common.ScreenStatus
import io.github.mrgsrylm.skso.viewmodel.SignInViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    navigateToHomeScreen: () -> Unit,
) {
    val signInVM by viewModel.signInState.observeAsState(initial = ScreenStatus.Loading)
    val signInState = signInVM

    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnimation = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "",
    )
    when {
        signInState is ScreenStatus.Success -> {
            LaunchedEffect(key1 = true) {
                startAnimation = true
                delay(2000)
                navigateToHomeScreen.invoke()
            }
            SplashContent(alpha = alphaAnimation.value)
        }

        signInState is ScreenStatus.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Failed to auto sign in")
            }
        }
    }

}


//@Preview(showBackground = false)
//@Composable
//fun SplashScreenPreview() {
//    SKSOTheme {
//        SplashContent(alpha = 1f)
//    }
//}