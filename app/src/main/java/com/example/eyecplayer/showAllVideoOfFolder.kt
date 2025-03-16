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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun showAllVideoOfFolder(selectedFolder: String ,context: Context,onBack : () -> Unit){
    val mediaFiles = remember { mutableStateListOf<MediaFile>() }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var sorter by remember { mutableStateOf(0) }
    LaunchedEffect(sorter) {
        mediaFiles.clear() // Clear previous list
        mediaFiles.addAll(getAllVideos(selectedFolder,context,sorter)) // Corrected function call
    }

        if (selectedVideoUri == null) {
            Column(Modifier.padding(16.dp)){
            Row(Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = { onBack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(text = selectedFolder, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Image(

                    painter = painterResource(id = R.drawable.baseline_sort_24),
                    contentDescription = "sort Icon",
                    modifier = Modifier.size(30.dp).clickable { sorter=(sorter+1)%6 }
                )
            }

            Text(
                text = "Videos",
                fontSize = 24.sp, // Use fontSize instead of size
                modifier = Modifier.padding(bottom = 12.dp) // Add spacing below title
            )
        LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mediaFiles.size) { index ->
                val mediaFile = mediaFiles[index]
                VideoItem(mediaFile, context) { uri ->
                    selectedVideoUri = uri // Set the selected video URI
                }
            }
        }
        }

        }
        else{
            VideoPlayer(source = selectedVideoUri!!.toString(), context = context) // Show video player
        }
}

@Composable
fun VideoItem(mediaFile: MediaFile, context: Context,onVideoClick: (Uri) -> Unit){
    Row(
        Modifier
            .fillMaxWidth().clip(RoundedCornerShape(4.dp)).background(Color.White.copy(alpha = 0.2f))
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
        Spacer(Modifier.width(12.dp))
        Column {
            Text(text = mediaFile.name)
            Text(text = durationformat(mediaFile.duration))
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