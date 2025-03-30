package com.example.eyecplayer.online

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eyecplayer.R
import com.example.eyecplayer.ui.theme.DarkGray1
import com.example.eyecplayer.ui.theme.White
@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Category,
        Screen.Watchlist,
        Screen.Profile
    )

    NavigationBar(
        modifier = Modifier.height(88.dp),
        containerColor = if(isSystemInDarkTheme()) DarkGray1 else White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = getIcon(screen, currentRoute == screen.route)),
                        contentDescription = screen.route, tint = if(isSystemInDarkTheme()) White else Color.Black
                    )
                },
                label = { Text(text = screen.route, style =MaterialTheme.typography.titleSmall) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting an item
                        restoreState = true
                        // Pop up to the start destination
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun getIcon(screen: Screen, isSelected: Boolean): Int {
    return when (screen) {
        Screen.Home -> if (!isSelected) R.drawable.home else R.drawable.home_filled
        Screen.Category -> if (!isSelected) R.drawable.category else R.drawable.categories_filled
        Screen.Watchlist -> if (!isSelected) R.drawable.watchlist else R.drawable.watchlist_filled
        Screen.Profile -> if (!isSelected) R.drawable.profile else R.drawable.filledprofile
    }
}
//@Preview
//@Composable
//private fun helllllo() {
//    val navController by rememberNavController()
//    BottomNavigationBar(NavController)
//}
