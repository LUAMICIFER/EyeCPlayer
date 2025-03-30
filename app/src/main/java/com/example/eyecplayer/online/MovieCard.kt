package com.example.eyecplayer.online

import androidx.camera.video.Quality
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.eyecplayer.ui.theme.DarkGray1
import com.example.eyecplayer.ui.theme.DarkGray2
import com.example.eyecplayer.ui.theme.DarkGray4
import com.example.eyecplayer.ui.theme.LightGray1
import com.example.eyecplayer.ui.theme.LightGray2
import com.example.eyecplayer.ui.theme.PrimaryRed
import com.example.eyecplayer.ui.theme.White

@Composable
fun MovieCard(imageUrl :String,movieName :String,quality :String,duration :String,year : String,genre :String){
    Box(
        Modifier.padding(16.dp)
            .clickable { TODO("add navigation route to the movie detail page with the imdbid only") }
            .clip(RoundedCornerShape(8.dp))
            .height(205.dp)
            .width(160.dp)){
        Column(
            Modifier
                .fillMaxSize()
                .background(
                    if (isSystemInDarkTheme()) {
                        DarkGray1
                    } else {
                        White
                    }
                )
                .padding(8.dp)) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Movie Thumbnail",
                modifier = Modifier
                    .width(260.dp) // Set width
                    .height(100.dp) // Set height
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()){
                Text(text = movieName, style = MaterialTheme.typography.headlineMedium.copy(fontSize = 14.sp), color = if(isSystemInDarkTheme()) White else Black )
                Box(contentAlignment = Alignment.Center, modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .width(22.dp)
                    .height(16.dp)
                    .background(DarkGray4)){
                    Text(text = quality, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp))
                }
            }
            Row(Modifier.width(125.dp).height(15.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                Text("${duration}m Left", style = MaterialTheme.typography.titleSmall.copy(fontSize = 10.sp), color = if(isSystemInDarkTheme()){ LightGray2 }else{ DarkGray2})
                Text("•", style = MaterialTheme.typography.titleSmall.copy(fontSize = 10.sp), color = DarkGray4)
                Text(text = year, style = MaterialTheme.typography.titleSmall.copy(fontSize = 10.sp), color = if(isSystemInDarkTheme()){ LightGray2 }else{ DarkGray2})
                Text("•", style = MaterialTheme.typography.titleSmall.copy(fontSize = 10.sp), color =  DarkGray4)
                Text(text = genre, style = MaterialTheme.typography.titleSmall.copy(fontSize = 10.sp), color = if(isSystemInDarkTheme()){ LightGray2 }else{ DarkGray2})
            }
            Spacer(Modifier.height(4.dp))
            Button(onClick = {},Modifier.width(140.dp),shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(
                PrimaryRed)) {
                Text(text = "Watch Now",color = White,style = MaterialTheme.typography.titleSmall)
            }
        }

    }
}
@Preview
@Composable
fun Hello(modifier: Modifier = Modifier) {
    MovieCard("https://terrypiercebooks.com/wp-content/uploads/2016/09/cropped-background-16-9.png","Movie Title","Hd","32","2012","Romance")
}