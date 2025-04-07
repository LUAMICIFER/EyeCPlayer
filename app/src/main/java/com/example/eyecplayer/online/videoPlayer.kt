package com.example.eyecplayer.online

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.eyecplayer.ui.theme.White

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    movieId: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hlsUrl by remember { mutableStateOf<String?>(null) }

    // Fetch movie URL asynchronously
    LaunchedEffect(movieId) {
        FirebaseManager.getMovieById(movieId) { movie ->
            hlsUrl = movie?.playableLinks?.getOrNull(0)
                ?: "https://m3u8.wafflehacker.io/m3u8-proxy?url=https%3A%2F%2Fs2.phim1280.tv%2F20231017%2F20MEMVbZ%2Findex.m3u8"
            Log.d("VideoPlayer", "Playing URL: $hlsUrl")
        }
    }

    // Create ExoPlayer only when hlsUrl is available
    hlsUrl?.let { url ->
        val exoPlayer = remember {
            SimpleExoPlayer.Builder(context).build().apply {
                val mediaSource = HlsMediaSource.Factory(
                    DefaultHttpDataSource.Factory()
                        .setAllowCrossProtocolRedirects(true)  // Important for HLS
                        .setConnectTimeoutMs(30_000)           // 30 seconds timeout
                        .setReadTimeoutMs(30_000)              // 30 seconds read timeout
                        .setUserAgent("ExoPlayer")             // Helps with server compatibility
                ).createMediaSource(MediaItem.fromUri(Uri.parse(url)))


                setMediaSource(mediaSource)
                prepare()
                playWhenReady = true
            }
        }

        // Cleanup when composable is removed
        DisposableEffect(Unit) {
            onDispose { exoPlayer.release() }
        }

        Box(modifier.fillMaxSize()) {
            // Video Player
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Back button

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
    }
}
