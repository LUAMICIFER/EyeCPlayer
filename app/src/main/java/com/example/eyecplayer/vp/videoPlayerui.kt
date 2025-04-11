package com.example.eyecplayer.vp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eyecplayer.CameraAnalysis
import com.example.eyecplayer.R
import com.example.eyecplayer.formatDuration
import com.example.eyecplayer.ui.theme.LightGray3
import com.example.eyecplayer.ui.theme.LightGray4
import com.example.eyecplayer.ui.theme.PrimaryRed
import com.example.eyecplayer.ui.theme.White
import com.example.eyecplayer.vp.VideoPlayerManager.lockToLandscape
import com.example.eyecplayer.vp.VideoPlayerManager.lockToPortrait
import com.example.eyecplayer.vp.VideoPlayerManager.mediaPlayer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@Composable
fun VideoPlayerUi(navController: NavController,name : String =""){
    var controlVisibility by remember { mutableStateOf(true) }
    var speed by remember { mutableFloatStateOf(1f) }
//    var name by remember { mutableStateOf("") }
    var audiodailogbox by remember { mutableStateOf(false) }
    var subdailogbox by remember {mutableStateOf(false)}
    val currentTime = remember { mutableStateOf(0L) }
    val isSubtitleEnabled = remember { mutableStateOf(VideoPlayerManager.isSubtitleEnabled()) }
    if (!VideoPlayerManager.isInitialized()) return
    val activity = (LocalContext.current as? Activity) // roation aaur screen on rakhne dono ke kaam aaya hai
    var userDetection by remember { mutableStateOf(false) }
    Box(){
        Row(Modifier.fillMaxSize()) {

            // LEFT SIDE: SPEED CONTROL (Swipe Up/Down) + Seek Backward (Double Tap)
            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { controlVisibility = !controlVisibility },
                            onDoubleTap = { VideoPlayerManager.seekBackward() },
                            onPress = {
                                coroutineScope {
                                    val job = launch {
                                        delay(200)
                                        VideoPlayerManager.setPlaybackSpeed(2f)
                                    }
                                    tryAwaitRelease()
                                    job.cancel()
                                    VideoPlayerManager.setPlaybackSpeed(1f)
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            val step = 0.3f
                            val threshold = 10f
                            if (dragAmount > threshold) {
                                // Swipe Down – Decrease speed
                                speed = (speed - step).coerceIn(0.5f, 3.0f)
                                VideoPlayerManager.setPlaybackSpeed(speed)
                            } else if (dragAmount < -threshold) {
                                // Swipe Up – Increase speed
                                speed = (speed + step).coerceIn(0.5f, 3.0f)
                                VideoPlayerManager.setPlaybackSpeed(speed)
                            }
                        }
                    }
            ) {}

            // RIGHT SIDE: VOLUME CONTROL (Swipe Up/Down) + Seek Forward (Double Tap)
            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { controlVisibility = !controlVisibility },
                            onDoubleTap = { VideoPlayerManager.seekForward() },
                            onPress = {
                                coroutineScope {
                                    val job = launch {
                                        delay(200)
                                        VideoPlayerManager.setPlaybackSpeed(2f)
                                    }
                                    tryAwaitRelease()
                                    job.cancel()
                                    VideoPlayerManager.setPlaybackSpeed(1f)
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            val threshold = 10f
                            val currentVolume = VideoPlayerManager.getVolume()
                            val step = 5 // 5% volume step

                            if (dragAmount > threshold) {
                                // Swipe Down – Decrease volume
                                val newVol = (currentVolume - step).coerceIn(0, 100)
                                VideoPlayerManager.setVolume(newVol)
                            } else if (dragAmount < -threshold) {
                                // Swipe Up – Increase volume
                                val newVol = (currentVolume + step).coerceIn(0, 100)
                                VideoPlayerManager.setVolume(newVol)
                            }
                        }
                    }
            ) {}
        }
        //iske upar gesture wala cheej implementated hai
        if(controlVisibility){
            LaunchedEffect(Unit) {
                while (true) {
                    currentTime.value = VideoPlayerManager.getCurrentTime()
                    delay(500L)
                }
            }
            Box(Modifier.fillMaxSize()) {
                Row(
                    Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() }, Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                            tint = White,
                            contentDescription = "Icon"
                        )
                    }
                    Text(
                        text = name,
                        color = White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        speed = if (speed >= 4f) 0.5f else (speed + 0.5f)
                        VideoPlayerManager.setPlaybackSpeed(speed)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_speed_24),
                            contentDescription = "speed control",
                            tint = LightGray3,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    IconButton(onClick = {
                        audiodailogbox = !audiodailogbox
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_audiotrack_24),
                            contentDescription = "Audio Track control",
                            tint = LightGray3,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    IconButton(onClick = {
                        if (isSubtitleEnabled.value) {
                            VideoPlayerManager.disableSubtitles()
                            isSubtitleEnabled.value = false
                        } else {
                            subdailogbox = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = if (isSubtitleEnabled.value) R.drawable.baseline_subtitles_off_24 else R.drawable.baseline_subtitles_24),
                            contentDescription = if (isSubtitleEnabled.value) "Disable Subtitles" else "Enable Subtitles", tint = White
                        )
                    }
//                    IconButton(onClick = { userDetection = !userDetection }) {
//                        Icon(
//                            painter = painterResource(
//                                id = if (userDetection) {
//                                    R.drawable.baseline_visibility_24
//                                } else {
//                                    R.drawable.baseline_visibility_off_24
//                                }
//                            ),
//                            contentDescription = "User Face Detection",
//                            tint = LightGray3,
//                            modifier = Modifier.size(40.dp)
//                        )
//                    }
//                    if (userDetection && VideoPlayerManager.isPlaying()) {
//                        CameraAnalysis(context,exoplayer)
//                    }

                }
                VideoControls()
                }
                if (audiodailogbox) {
                    var tracks = VideoPlayerManager.getAudioTrack()

                    AlertDialog(
                        onDismissRequest = { audiodailogbox = false },
                        title = { Text("Choose Audio Track") },
                        text = {
                            Column {
                                tracks.forEach { track ->
                                    Text(
                                        text = track.name ?: "Unknown",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                VideoPlayerManager.changeAudioTrack(trackId = track.id)
                                                audiodailogbox = false
                                            }
                                            .padding(8.dp)
                                    )

                                }
                            }
                        },
                        confirmButton = {},
                        dismissButton = {
                            TextButton(onClick = { audiodailogbox = false }) {
                                Text("Close")
                            }
                        }
                    )
                }
            if (subdailogbox) {
                val subtitleTracks = VideoPlayerManager.getSubtitleTracks()
                AlertDialog(
                    onDismissRequest = { subdailogbox = false },
                    title = { Text("Choose Subtitle") },
                    text = {
                        Column {
                            subtitleTracks.forEach { track ->
                                Text(
                                    text = track.name ?: "Unknown",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
//                                            mediaPlayer.setSpuTrack(track.id)
                                            VideoPlayerManager.setSubtitleTrack(track.id)
                                            isSubtitleEnabled.value = true
                                            subdailogbox = false
                                        }
                                        .padding(8.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(onClick = { subdailogbox = false }) {
                            Text("Close")
                        }
                    }
                )
            }
                Column(Modifier.align(Alignment.BottomCenter)) {
                    Row(
                        modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val totalDuration = VideoPlayerManager.getTotalDuration()

                        Text(text= formatDuration(currentTime.value), color = Color.White ,modifier = Modifier.padding(start = 8.dp,end=8.dp))

                        Slider(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(0.8f),
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryRed,
                                activeTrackColor = PrimaryRed,
                                inactiveTrackColor = LightGray4
                            ),
                            value = if (totalDuration != 0L) currentTime.value / totalDuration.toFloat() else 0f,
                            onValueChange = { newValue ->
                                val newTime = (newValue * totalDuration).toLong()
                                VideoPlayerManager.seekTo(newTime)
                            }
                        )

                        Text(formatDuration(totalDuration), color = Color.White, modifier = Modifier.padding(start = 8.dp))
                    }
                    Row() {
                        val configuration = LocalConfiguration.current
                        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

//                        RotationToggleButton(isLandscape = isLandscape)
                        IconButton(onClick = {}) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_lock_24),
                                contentDescription = "lcok", tint = White
                            )
                        }
                        val config = LocalConfiguration.current
                        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
                            IconButton(onClick = {
                                activity?.requestedOrientation =
                                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_screen_rotation_24),
                                    tint = LightGray3,
                                    contentDescription = "Rotate Screen"
                                )
//                                Text("Potrait",color = Color.White)
                            }
//                            Text("Potrait",color = Color.White)

                        } else {
                            IconButton(onClick = {
                                activity?.requestedOrientation =
                                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_screen_rotation_24),
                                    tint = LightGray3,
                                    contentDescription = "Rotate Screen"
                                )


                            }
                        }
//                        Text("Landscape",color = Color.White)
                        IconButton(onClick = {}) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_headphones_24),
                                contentDescription = "only audio", tint = White
                            )
                        }
                    }

                    }
                }


            }



        }
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun VideoControls() {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    // State to track if media is playing
    val isPlaying = remember { mutableStateOf(VideoPlayerManager.isPlaying()) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(if (isPortrait) 0.8f else 0.5f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Backward
            IconButton(
                onClick = { VideoPlayerManager.seekBackward() },
                modifier = Modifier
                    .clip(CircleShape)
                    .size(64.dp)
                    .background(Color.Black.copy(alpha = 0.6f))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_fast_rewind_24),
                    tint = LightGray4,
                    contentDescription = "Backward"
                )
            }

            // Play / Pause
            IconButton(
                onClick = {
                    if (isPlaying.value) {
                        VideoPlayerManager.pause()
                    } else {
                        VideoPlayerManager.play()
                    }
                    isPlaying.value = !isPlaying.value
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .size(80.dp)
                    .background(Color.Black.copy(alpha = 0.6f))
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isPlaying.value)
                            R.drawable.baseline_pause_24
                        else
                            R.drawable.baseline_play_arrow_24
                    ),
                    tint = LightGray4,
                    contentDescription = if (isPlaying.value) "Pause" else "Play"
                )
            }

            // Forward
            IconButton(
                onClick = { VideoPlayerManager.seekForward() },
                modifier = Modifier
                    .clip(CircleShape)
                    .size(64.dp)
                    .background(Color.Black.copy(alpha = 0.6f))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_fast_forward_24),
                    tint = LightGray4,
                    contentDescription = "Forward"
                )
            }
        }
    }
}

//@Preview
//@Composable
//private fun hel() {
//    VideoPlayerUi(navController = )
//
//}