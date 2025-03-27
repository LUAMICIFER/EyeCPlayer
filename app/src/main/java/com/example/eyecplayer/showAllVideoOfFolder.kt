package com.example.eyecplayer

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eyecplayer.ui.theme.DarkGray1
import com.example.eyecplayer.ui.theme.DarkGray2
import com.example.eyecplayer.ui.theme.DarkGray3
import com.example.eyecplayer.ui.theme.LightGray3
import com.example.eyecplayer.ui.theme.LightGray4
import com.example.eyecplayer.ui.theme.PrimaryRed
import com.example.eyecplayer.ui.theme.White

@Composable
fun showAllVideoOfFolder(navController: NavController,selectedFolder: String){
    val context = LocalContext.current
    val mediaFiles = remember { mutableStateListOf<MediaFile>() }
//    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var sorter by remember { mutableIntStateOf(0) }
    LaunchedEffect(sorter) {
        mediaFiles.clear() // Clear previous list
        mediaFiles.addAll(getAllVideos(selectedFolder,context,sorter)) // Corrected function call
    }

    Column(
        Modifier.fillMaxSize()
            .background(if (isSystemInDarkTheme()) Color.Black else Color.White)
            .padding(16.dp)){
            Row(Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
                IconButton(onClick = {navController.popBackStack()}) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",tint = if(isSystemInDarkTheme()) Color.White else Color.Black , modifier = Modifier.size(24.dp))
                }
                //folder path
                Text(
                    text = selectedFolder,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = {sorter = (sorter + 1) % 6}){
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_sort_24),
                        contentDescription = "sort Icon",
                        tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }

        Text(text = "Videos", style = MaterialTheme.typography.displayLarge)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(mediaFiles.size) { index ->
                val mediaFile = mediaFiles[index]
                VideoItem(mediaFile, context) { uri ->
                    navController.navigate(Routes.player + "/${Uri.encode(uri.toString())}") //Ensures that characters like /, ?, &, and = are safely encoded.
                    //Prevents crashes or incorrect navigation due to malformed URLs.
                }
            }
        }
    }
}

@Composable
fun VideoItem(mediaFile: MediaFile, context: Context,onVideoClick: (Uri) -> Unit){
    Row(
        Modifier
            .fillMaxWidth()
            .graphicsLayer {
                shadowElevation = 8.dp.toPx()
                shape = RoundedCornerShape(8.dp)
            }
            .clip(RoundedCornerShape(4.dp))
            .background(
                color = if (isSystemInDarkTheme()) DarkGray2 else White
            )
            .clickable { onVideoClick(mediaFile.uri) }) {
        val thumbnail = remember { mutableStateOf<Bitmap?>(null) }

        LaunchedEffect(mediaFile.uri) {
            thumbnail.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getVideoThumbnail(context.contentResolver, mediaFile.uri)
            } else {
                getVideoThumbnail(mediaFile.uri)
            }
        }
        thumbnail.value?.let { bmp ->
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Video Thumbnail",
                modifier = Modifier.size(80.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(text = mediaFile.name,style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,color =if (isSystemInDarkTheme()) White else DarkGray1,maxLines= 2,overflow = TextOverflow.Ellipsis)
            Text(text = durationformat(mediaFile.duration), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color =if (isSystemInDarkTheme()) LightGray3 else DarkGray3)
        }
    }
}



fun durationformat(duration : Long): String{
    val hours = (duration / 1000) / 3600
    val minutes = (duration / 1000) / 60
    val seconds = (duration / 1000) % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds) // HH:MM:SS format
    } else {
        String.format("%02d:%02d", minutes, seconds) // MM:SS format if duration is less than an hour
    }

}


//jo 29 se upar hoga uske liye hai
@RequiresApi(Build.VERSION_CODES.Q)
fun getVideoThumbnail(contentResolver: ContentResolver, videoUri: Uri): Bitmap? {
    return try {
        contentResolver.loadThumbnail(videoUri, Size(120, 120), null)
    } catch (e: Exception) {
        null
    }
}
//jo 29 se neeche hoga uske liye hai
fun getVideoThumbnail(videoUri: Uri): Bitmap? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(videoUri.toString())
        val bitmap = retriever.getFrameAtTime(1000000) // Get frame at 1 sec
        retriever.release()
        bitmap
    } catch (e: Exception) {
        retriever.release()
        null
    }
}