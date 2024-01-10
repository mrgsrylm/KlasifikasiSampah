package io.github.mrgsrylm.skso.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomNavBar(
    modifier: Modifier = Modifier,
    badgeState: Int,
    navController: NavHostController,
    bottomBarState: MutableState<Boolean>,
) {

    val items = listOf(
        AppNavItem.Home,
        AppNavItem.Log,
        AppNavItem.Notification
    )

    NavigationBar(modifier) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                onClick = {
                    navController.navigate(item.route) {
                        navController.currentDestination?.route.let { route ->
                            popUpTo(route ?: AppNavItem.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                icon = {
                    if (item == AppNavItem.Notification) {
                        IconWithBadge(
                            badge = badgeState,
                            icon = item.icon,
                            desc = item.desc,
                            modifier = Modifier.padding(4.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = stringResource(id = item.desc),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
            )
        }
    }

}

@Composable
fun IconWithBadge(
    badge: Int,
    icon: Int,
    desc: Int,
    modifier: Modifier = Modifier,
    tint: Color,
) {
    Box(modifier = Modifier.size(36.dp)) {
        Icon(
            painter = painterResource(id = icon),
            modifier = modifier.align(
                Alignment.BottomCenter,
            ),
            tint = tint,
            contentDescription = stringResource(id = desc),
        )

        if (badge != 0) {
            Text(
                text = "$badge",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
                    .align(Alignment.TopEnd)
                    .size(16.dp),
            )
        }
    }
}

//@Composable
//fun RowScope.AddItem(
//    screen: AppNavItem
//) {
//    NavigationBarItem(
//        selected = true,
//        alwaysShowLabel = false,
//        colors = NavigationBarItemDefaults.colors(),
//        label = {
//            Text(text = screen.title)
//        },
//        icon = {
//            Icon(
//                painter = painterResource(id = screen.icon),
//                contentDescription = stringResource(id = screen.desc)
//            )
//        },
//        onClick = {
//
//        }
//    )
//}

