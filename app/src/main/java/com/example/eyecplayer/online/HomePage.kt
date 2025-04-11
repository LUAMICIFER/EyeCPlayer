package com.example.eyecplayer.online

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.eyecplayer.R
import com.example.eyecplayer.ui.theme.White
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.eyecplayer.vp.LVideoPlayer
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestoreSettings

import kotlinx.coroutines.launch
//@Composable
//fun HomeScreen(){
////    val navController = rememberNavController()
////
////    Scaffold(
////        bottomBar = { BottomNavigationBar(navController) }
////    ) { innerPadding ->
////        NavHost(
////            navController = navController,
////            startDestination = Screen.Home.route,
////            modifier = Modifier.padding(innerPadding)
////        ) {
////            composable(Screen.Home.route) { homeScreenui(navController) }
////            composable(Screen.Category.route) { CategoryScreen(navController) }
////            composable(Screen.Watchlist.route) { WatchlistScreen(navController) }
////            composable(Screen.Profile.route) { ProfileScreen(navController) }
////        }
////    }
//    val navController = rememberNavController()
//
//    Scaffold(
//        bottomBar = { BottomNavigationBar(navController) }
//    ) { innerPadding ->
//        NavHost(
//            navController = navController,
//            startDestination = Screen.Home.route,
//            modifier = Modifier.padding(innerPadding)
//        ) {
//            composable(Screen.Home.route) { HomeScreen(navController) }
//            composable(Screen.Category.route) { CategoryScreen(navController) }
//            composable(Screen.Watchlist.route) { WatchlistScreen(navController) }
//            composable(Screen.Profile.route) { ProfileScreen(navController) }
//        }
//    }
//}
@Composable
fun HomeScreen(a: NavController) {
    val navController = rememberNavController()
    // Track current back stack entry
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define routes where bottom bar should be hidden
    val hideBottomBarRoutes = listOf("VIDEOPLAYER/{id}")

    Scaffold(
        bottomBar = {
            if (currentRoute !in hideBottomBarRoutes &&
                !currentRoute.orEmpty().startsWith("VIDEOPLAYER/")) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "HOME",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("HOME") { homeScreenui(navController) } // Fix recursion
            composable("CATEGORY/{genere}") {
                val genre = it.arguments?.getString("genere")
                CategoryScreen(navController, selected = genre?:"") }
            composable("WATCHLIST") { WatchlistScreen(navController) }
            composable("PROFILE") { ProfileScreen(navController) }
            composable("DETAILPAGE/{id}") {
                val Id = it.arguments?.getString("id")
                DescriptionScreen(navController,Id?:"tt1981128")}
            composable("VIDEOPLAYER/{id}"){
                val Id = it.arguments?.getString("id")
//                VideoPlayerScreen(navController=navController, movieId =Id?:"tt1981128")
                LVideoPlayer(source = Id?:"tt1981128",navController=navController)
            }

        }
    }
}
@Composable
fun homeScreenui(navController: NavController) {
//    val navController = rememberNavController()
//    NavHost(navController = navController, startDestination =)
    Firebase.firestore.firestoreSettings = firestoreSettings { // to increase the time of runout
        setHost("firestore.googleapis.com")
        setSslEnabled(true)
        setPersistenceEnabled(true)
    }

    var recentMovies by remember { mutableStateOf<List<Movie>>(emptyList()) }

    // Store movies by genre (e.g., "action" -> List<Movie>)
    var genreMovies by remember { mutableStateOf<Map<String, List<Movie>>>(emptyMap()) }

    // Track which genres have been fetched
    val fetchedGenres = remember { mutableStateOf(mutableSetOf<String>()) }

    val listState = rememberLazyListState()

    // List of genres to load
    val genresToLoad = listOf("action", "drama", "comedy", "thriller")

    // Fetch recent movies when screen loads
    LaunchedEffect(Unit) {
        recentMovies = FirebaseManager.getRecentMovies()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) Color.Black else White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource( id =R.drawable.baseline_play_arrow_24), contentDescription = "Icon",
                    Modifier.size(48.dp))
                IconButton(
                    onClick = { TODO() }, Modifier.size(24.dp)
                ) {
                    Icon(painter = painterResource( id =R.drawable.baseline_play_arrow_24), contentDescription = "Icon")
                }
            }

            LazyColumn(state = listState) {
                if (recentMovies.isNotEmpty()) {
                    item { hero(recentMovies) }
                    item { lazyRowWheader(navController,"Recently Added", recentMovies) }
                }

                // Load each genre dynamically
                genresToLoad.forEach { genre ->
                    item {
                        val isGenreVisible = remember {
                            derivedStateOf {
                                val layoutInfo = listState.layoutInfo
                                layoutInfo.visibleItemsInfo.any { it.index == layoutInfo.totalItemsCount - 1 }
                            }
                        }

                        // Fetch movies only if the genre is visible & not already fetched
                        if (isGenreVisible.value && genre !in fetchedGenres.value) {
                            fetchedGenres.value.add(genre)
                            FirebaseManager.getMoviesByGenre(genre) { movies ->
                                genreMovies = genreMovies + (genre to movies)
                            }
                        }
                        if (genreMovies[genre]?.isNotEmpty() == true) {
                            lazyRowWheader(navController,genre.capitalize(), genreMovies[genre]!!)
                        }
                    }
                }
            }
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



@Preview(showBackground = true)
@Composable
private fun homePagepreview() {
    HomeScreen(rememberNavController())
}
