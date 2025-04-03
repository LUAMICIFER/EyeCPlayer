package com.example.eyecplayer.online
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.eyecplayer.ui.theme.DarkGray4
import com.example.eyecplayer.ui.theme.HighlightMedium
import com.example.eyecplayer.ui.theme.PrimaryRed
import com.example.eyecplayer.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun hero(movies: List<Movie>) {
    val pagerState = rememberPagerState(pageCount = { movies.size })
    Box{
        Column{
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(220.dp)
            ) { page ->
                val movie = movies[page]

                Box {
                    AsyncImage(
                        model = movie.thumbnailLink,
                        contentDescription = "${movie.title} backdrop",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Text(text = movie.releaseDate.take(4), color = White, style = MaterialTheme.typography.titleSmall)
                            Text(text = "•", color = White, style = MaterialTheme.typography.titleSmall)

                            Text(text = movie.genres.first(), color = White, style = MaterialTheme.typography.titleSmall)
                            Text(text = "•", color = White, style = MaterialTheme.typography.titleSmall)

                            Text(text = movie.genres.getOrNull(1) ?: "", color = White, style = MaterialTheme.typography.titleSmall)
                            Text(text = "•", color = White, style = MaterialTheme.typography.titleSmall)

                            Text(text = "UHD", color = White, style = MaterialTheme.typography.titleSmall)

                        }
                        Text(text = movie.title, maxLines = 1, color = White, style = MaterialTheme.typography.headlineLarge)
                        Button(onClick = {},Modifier.width(140.dp),shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(
                            PrimaryRed)) {
                            Text(text = "Watch Now",color = White,style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }

            }
            DotIndicator(
                pageCount = movies.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally), activeColor = if(isSystemInDarkTheme())PrimaryRed else HighlightMedium, inactiveColor = DarkGray4
            )

            LaunchedEffect(pagerState) {
                while (true) {
                    delay(3000)
                    if (pagerState.pageCount > 0) { // Prevent divide by zero
                        val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                        try {
                            pagerState.animateScrollToPage(
                                page = nextPage,
                                animationSpec = tween(
                                    durationMillis = 500,
                                    easing = LinearEasing
                                )
                            )
                        } catch (e: Exception) {
                            // Handle cancellation
                        }
                    }
                }
            }

        }
    }
}
@Composable
fun DotIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = White,
    inactiveColor: Color = DarkGray4
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(pageCount) { page ->
            Text(
                text = if(page == currentPage) "•" else "◦", // You can also use "○" or "●" or "⋯"
                color = if (page == currentPage) activeColor else inactiveColor,
                modifier = Modifier.padding(horizontal = 4.dp),
                style = LocalTextStyle.current.copy(
                    fontSize = if (page == currentPage) 32.sp else 24.sp
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun preasd() {
    val sampleMovies = listOf(
        Movie(
            imdbId = "tt1375666",
            title = "Inception",
            releaseDate = "2010-07-16",
            genres = listOf("Sci-Fi", "Action", "Thriller"),
            playableLinks = listOf("https://example.com/play1"),
            thumbnailLink = "https://image.tmdb.org/t/p/w1280/8riQbqiZ0OZzQ7ggd6E6dODqQfO.jpg"
        ),
        Movie(
            imdbId = "tt0816692",
            title = "Interstellar",
            releaseDate = "2014-11-07",
            genres = listOf("Sci-Fi", "Adventure", "Drama"),
            playableLinks = listOf("https://example.com/play2"),
            thumbnailLink = "https://image.tmdb.org/t/p/w1280/5aXwDCf0gZq3vT5OacFDXVtk4MR.jpg"
        ),
        Movie(
            imdbId = "tt0133093",
            title = "The Matrix",
            releaseDate = "1999-03-31",
            genres = listOf("Sci-Fi", "Action"),
            playableLinks = listOf("https://example.com/play3"),
            thumbnailLink = "https://image.tmdb.org/t/p/w1280/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg"
        ),
        Movie(
            imdbId = "tt0111161",
            title = "The Shawshank Redemption",
            releaseDate = "1994-09-23",
            genres = listOf("Drama"),
            playableLinks = listOf("https://example.com/play4"),
            thumbnailLink = "https://image.tmdb.org/t/p/w1280/j9XKiZrVeViAixVRzCta7h1kU3w.jpg"
        ),
        Movie(
            imdbId = "tt0068646",
            title = "The Godfather",
            releaseDate = "1972-03-24",
            genres = listOf("Crime", "Drama"),
            playableLinks = listOf("https://example.com/play5"),
            thumbnailLink = "https://image.tmdb.org/t/p/w1280/rSPw7tgCH9c6NqICZef4kZjFOQ5.jpg"
        ),
        Movie(
            imdbId = "tt0468569",
            title = "The Dark Knight",
            releaseDate = "2008-07-18",
            genres = listOf("Action", "Crime", "Drama"),
            playableLinks = listOf("https://example.com/play6"),
            thumbnailLink = "https://image.tmdb.org/t/p/w1280/h3jYanWMEJq6JJsCopy1h7cT2Hs.jpg"
        ),
        Movie(
            imdbId = "tt0109830",
            title = "Forrest Gump",
            releaseDate = "1994-07-06",
            genres = listOf("Drama", "Romance"),
            playableLinks = listOf("https://example.com/play7"),
            thumbnailLink = "https://image.tmdb.org/t/p/w1280/ctOEhQiFIHWkiaYp8b0ibwetHvP.jpg"
        ),
        Movie(
            imdbId = "tt0167260",
            title = "The Lord of the Rings: The Two Towers",
            releaseDate = "2002-12-18",
            genres = listOf("Adventure", "Fantasy", "Action"),
            playableLinks = listOf("https://example.com/play8"),
            thumbnailLink = "https://image.tmdb.org/t/p/w1280/5BP6zVCFl2yGzJ6O0Q8kF9VNSyZ.jpg"
        )
    )
    hero(sampleMovies)
}

