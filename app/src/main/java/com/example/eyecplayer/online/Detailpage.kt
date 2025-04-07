package com.example.eyecplayer.online

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.eyecplayer.ui.theme.DarkGray1
import com.example.eyecplayer.ui.theme.PrimaryRed
import com.example.eyecplayer.ui.theme.PrimaryRed75
import com.example.eyecplayer.ui.theme.White
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


@Composable
fun StarRating(rating: Float, maxRating: Float = 10f, stars: Int = 5) {
    val starsToFill = (rating / maxRating * stars).toInt()

    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(stars) { index ->
            Icon(
                imageVector = if (index < starsToFill) Icons.Default.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (index < starsToFill) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "IMDB",
            color = Color.Red,
            fontSize = 12.sp
        )
    }
}

@Composable
fun DescriptionScreen(navController: NavController, imdbId: String ) {
    val textColor = if (isSystemInDarkTheme()) White else Color.Black
    var isLoading by remember { mutableStateOf(true) }
    var movieData by remember { mutableStateOf<MovieResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(imdbId) {
        try {
            movieData = RetrofitClient.instance.getMovieDetails(imdbId)
            isLoading = false
        } catch (e: Exception) {
            errorMessage = e.message ?: "Unknown error occurred"
            isLoading = false
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) DarkGray1 else White)
            .padding(16.dp)
    ) {
        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            errorMessage != null -> Text(
                text = errorMessage!!,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
            movieData != null -> {
                val movie = movieData!!
                val animatedAlpha = remember { Animatable(0f) }
                val animatedY = remember { Animatable(50f) }

                LaunchedEffect(movieData) {
                    coroutineScope.launch {
                        animatedAlpha.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 800)
                        )
                        animatedY.animateTo(
                            targetValue = 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    }
                }

                Column {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back",tint= textColor,
                            modifier = Modifier.size(32.dp))
                    }

                    Box(Modifier.fillMaxSize().weight(6f)) {
                        AsyncImage(
                            model = movie.poster,
                            contentDescription = "Movie Poster",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Transparent,
                                            Color.Black
                                        ),
                                        startY = 300f
                                    )
                                ),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .graphicsLayer {
                                        alpha = animatedAlpha.value
                                        translationY = animatedY.value
                                    }
                            ) {
                                Text(
                                    text = movie.title,
                                    color = Color.White,
                                    style = MaterialTheme.typography.displayLarge,
//                                    fontSize = 24.sp,
//                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.animateContentSize()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = movie.year,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "•",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = movie.genre,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 16.sp,
                                        maxLines = 1,
                                        modifier = Modifier.animateContentSize()
                                    )
                                }
                            }
                        }
                    }

                    Box(Modifier.fillMaxSize().weight(2.5f)) {
                        Column {
                            Spacer(modifier = Modifier.height(32.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { navController.navigate("VIDEOPLAYER/${movie.imdbID}")},
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(48.dp)
                                        ,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PrimaryRed,
                                        contentColor = Color.White,
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Watch Now",
//                                            fontSize = 14.sp,
                                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp)
                                        )
                                    }
                                }
                                StarRating(
                                    rating = movie.imdbRating.toFloatOrNull() ?: 0f,
                                    maxRating = 10f
                                )
                            }

                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(top = 16.dp)
                                    .shadow(60.dp, spotColor = Color.DarkGray)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .verticalScroll(rememberScrollState())
                                        .padding(16.dp)
                                        .fillMaxSize()
                                        .animateContentSize()
                                ) {
                                    var textOpen by remember { mutableStateOf(false) }

                                    Text(
                                        text = movie.plot,
                                        maxLines = if (textOpen) Int.MAX_VALUE else 2,
                                        overflow = TextOverflow.Ellipsis,
                                        color = textColor,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                textOpen = !textOpen
                                            }
                                    )
                                    if(!textOpen){
                                        Text(text = "Read More",color = textColor, style = MaterialTheme.typography.titleSmall)

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

interface OMDBService {
    @GET("/")
    suspend fun getMovieDetails(
        @Query("i") imdbId: String,
        @Query("apikey") apiKey: String = "a86450f7",
        @Query("plot") plot: String = "full"
    ): MovieResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://www.omdbapi.com/"
    val instance: OMDBService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OMDBService::class.java)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDescriptionScreen() {
    DescriptionScreen(
        navController = NavController(LocalContext.current),
        imdbId = "tt1375666"
    )
}
