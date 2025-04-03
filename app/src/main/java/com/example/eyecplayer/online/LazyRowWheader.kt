package com.example.eyecplayer.online

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eyecplayer.R
@Composable
fun lazyRowWheader(header: String, movies: List<Movie>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = header,
                style = MaterialTheme.typography.displaySmall,
                color = if(isSystemInDarkTheme()) Color.White else Color.Black
            )
            IconButton(
                onClick = { /* Handle "See All" click */ },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.right),
                    contentDescription = "See All", tint = if(isSystemInDarkTheme()) Color.White else Color.Black
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(movies) { movie ->  // Use actual movie list size
                val secondGenre = movie.genres.getOrNull(1)
                MovieCard(

                    imageUrl = movie.thumbnailLink,
                    movieName = movie.title,
                    quality = "HD", // Default since Movie class lacks quality
                    duration = secondGenre ?: "Unknown",
                    year = movie.releaseDate,
                    genre = movie.genres.firstOrNull() ?: "Unknown" // Safe access
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun check() {
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
        ),
        Movie(
            imdbId = "tt0076759",
            title = "Star Wars: Episode IV - A New Hope",
            releaseDate = "1977-05-25",
            genres = listOf("Adventure", "Action", "Sci-Fi"),
            playableLinks = listOf("https://example.com/play9"),
            thumbnailLink = "https://image.tmdb.org/t/p/w1280/vw6m3hQKvT1PzVbPjJqJyjZFQAU.jpg"
        ),
        Movie(
            imdbId = "tt0088763",
            title = "Back to the Future",
            releaseDate = "1985-07-03",
            genres = listOf("Adventure", "Comedy", "Sci-Fi"),
            playableLinks = listOf("https://example.com/play10"),
            thumbnailLink = "https://image.tmdb.org/t/p/w1280/6yb7QVvU1Kxu7Glcj5xW4cApRsd.jpg"
        )
    )
    lazyRowWheader("Continue Watching",sampleMovies)
}