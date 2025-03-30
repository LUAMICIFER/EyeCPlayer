package com.example.eyecplayer.online

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eyecplayer.R
@Composable
fun lazyRowWheader(header: String ,movies : List<>) {

    Column{
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = header, style = MaterialTheme.typography.displaySmall)
            IconButton(
                onClick = { TODO() }, Modifier.size(24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.right),
                    contentDescription = "search Icon"
                )
            }
        }
        LazyRow {
            items(10) { movie ->
            MovieCard(
                imageUrl = movie.imageUrl,
                movieName = movie.name,
                quality = movie.quality,
                duration = movie.duration,
                year = movie.year,
                genre = movie.genre
            )
        }

        }
    }

}

@Preview(showBackground = true)
@Composable
private fun check() {
    lazyRowWheader("Continue Watching")
}