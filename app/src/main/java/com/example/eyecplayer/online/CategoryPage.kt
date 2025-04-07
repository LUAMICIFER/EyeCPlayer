package com.example.eyecplayer.online

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.eyecplayer.R
import com.example.eyecplayer.ui.theme.White
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.magnifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LottieAnimationExample() {
    // Load the Lottie animation file
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.work))
//    Text(text ="composition is: $composition", fontSize = 32.sp,color = Color.Red)
    // Create an animation state
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever // Set the number of loops
    )

    // Display the animation
    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = Modifier.size(400.dp)
    )

}
@Composable
fun CategoryScreen(navController: NavController, selected: String = "") {
    var selec by remember { mutableStateOf(selected) }
    var movies by remember { mutableStateOf(emptyList<Movie>()) }

    LaunchedEffect(selec) {
        if (selec.isNotEmpty()) {
            FirebaseManager.getMoviesByGenre(selec) {
                movies = it
            }
        }
    }

    Box(Modifier
        .fillMaxSize()
        .background(if (isSystemInDarkTheme()) Color.Black else White)) {
        Column(Modifier.padding(16.dp)) {
            // Top bar
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                    contentDescription = "Icon",
                    Modifier.size(48.dp),
                    tint = if (isSystemInDarkTheme()) White else Color.Black
                )
                Text("Categories", style = MaterialTheme.typography.displaySmall)
                IconButton(onClick = { /*TODO*/ }, Modifier.size(24.dp)) {
                    Icon(painter = painterResource(id = R.drawable.search), contentDescription = "Icon")
                }
            }

            // Category chips
            val categories = listOf("action", "drama", "comedy", "thriller", "adventure", "horror", "romance", "sci-fi", "animation", "documentary", "fantasy", "adult", "family", "all")
            LazyRow {
                items(categories) { category ->
                    Text(
                        text = category,
                        modifier = Modifier
                            .clickable {
                                selec = ""
                                selec = category
                            }
                            .padding(8.dp)
                            .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        color = Color.White
                    )
                }
            }

                if (selec.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center // Or any size that fits well
                    ) {
                        LottieAnimationExample()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Select a Genre",
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                } else {
                    MovieGrid(movies = movies, navController = navController, itemsPerRow = 2)
                }

            // Movie grid or animation

        }

    }
}


@Composable
fun MovieGrid(movies: List<Movie>, navController: NavController, itemsPerRow: Int = 2) {
    val rows = movies.chunked(itemsPerRow)

    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(rows) { rowMovies ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowMovies.forEach { movie ->
                    MovieCard(
                        navController = navController,
                        imdb = movie.imdbId,
                        imageUrl = movie.thumbnailLink,
                        movieName = movie.title,
                        quality = "HD", // or any default/fixed value since it's not in your data class
                        duration = movie.genres[1], // default/fixed value or calculate if available
                        year = movie.releaseDate,
                        genre = movie.genres.firstOrNull() ?: "Unknown"
                    )
                }

                // Fill empty space if row is not complete
                repeat(itemsPerRow - rowMovies.size) {
                    Spacer(modifier = Modifier.width(160.dp)) // same as card width
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun sh() {
    val nav = rememberNavController()
    CategoryScreen(nav)
    
}