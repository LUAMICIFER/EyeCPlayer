package com.example.eyecplayer.online

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eyecplayer.R

@Composable
fun HomeScreen(){
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { homeScreenui(navController) }
            composable(Screen.Category.route) { CategoryScreen(navController) }
            composable(Screen.Watchlist.route) { WatchlistScreen(navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController) }
        }
    }
}
@Composable
fun homeScreenui(navController: NavController) {
//    TODO("Not yet implemented")
    Box(Modifier.fillMaxSize().padding(16.dp)){
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                contentDescription = "Icon",Modifier.size(48.dp)
            )
            IconButton(
                onClick = { TODO() },Modifier.size(24.dp)
            ) {Image(painter = painterResource(id = R.drawable.search), contentDescription = "search Icon") }
        }


    }

}

@Composable
fun ProfileScreen(navController: NavController) {
    TODO("Not yet implemented")
}

@Composable
fun WatchlistScreen(navController: NavController) {
    TODO("Not yet implemented")
}

@Composable
fun CategoryScreen(navController: NavController) {
    TODO("Not yet implemented")
}

@Preview(showBackground = true)
@Composable
private fun homePagepreview() {
    HomeScreen()
}