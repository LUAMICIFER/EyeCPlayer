package com.example.eyecplayer.vp

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.eyecplayer.DataStoreManager
import com.example.eyecplayer.online.FirebaseManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.videolan.libvlc.util.VLCVideoLayout

//@Composable
//fun LVideoPlayer(
//    source: String,
//    navController: NavController,name :String =""
//) {
//    val context = LocalContext.current
//    var videoLayout by remember { mutableStateOf<VLCVideoLayout?>(null) }
//    var isInitialized by remember { mutableStateOf(false) }
//
//    // ✅ Initialize DataStoreManager here
//    LaunchedEffect(Unit) {
//        DataStoreManager.init(context)
//    }
//    if (source.startsWith("tt")) {
//        FirebaseManager.getMovieById(source) { movie ->
//            val playableLinks = movie?.playableLinks ?: emptyList()
//            if (playableLinks.isNotEmpty()) {
//                VideoPlayerManager.initialize(context)
//
//                videoLayout?.let {
//                    VideoPlayerManager.mediaPlayer.attachViews(it, null, false, false)
//                }
//
//                VideoPlayerManager.setMedia(context, source = playableLinks[0])
//                VideoPlayerManager.play()
//                isInitialized = true
//            }
//        }
//    } else {
//        LaunchedEffect(source) {
//            VideoPlayerManager.initialize(context)
//
//            videoLayout?.let {
//                VideoPlayerManager.mediaPlayer.attachViews(it, null, false, false)
//            }
//            VideoPlayerManager.setMedia(context, source)
//            VideoPlayerManager.play()
//            isInitialized = true
//        }
//    }
//
//    DisposableEffect(Unit) {
//        onDispose {
//            if (isInitialized) {
//                VideoPlayerManager.mediaPlayer.detachViews()
//                VideoPlayerManager.stop()
//                VideoPlayerManager.release()
//            }
//        }
//    }
//
//    Box {
//        AndroidView(
//            factory = { ctx ->
//                VLCVideoLayout(ctx).apply {
//                    keepScreenOn = true
//                    videoLayout = this
//                }
//            },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        if (isInitialized) {
//            VideoPlayerUi(navController)
//            LaunchedEffect(Unit){
//                if (DataStoreManager.getVideoUri() == source) {
//                    DataStoreManager.getVideoDuration()?.let { VideoPlayerManager.seekTo(it) }
//                }
//            }
//            LaunchedEffect(Unit) {
//                DataStoreManager.saveVideo(source)
//            }
//            LaunchedEffect(Unit) {
//                while (true){
//                    delay(1000)
//                    DataStoreManager.saveDurationData(VideoPlayerManager.getCurrentTime())
//                }
//            }
//        }
//    }
//}
@Preview
@Composable
private fun hf() {

    LVideoPlayer("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
        rememberNavController()
    )

}
@Composable
fun LVideoPlayer(
    source: String,
    navController: NavController,
    name: String = ""
) {
    val context = LocalContext.current
    var videoLayout by remember { mutableStateOf<VLCVideoLayout?>(null) }
    var isInitialized by remember { mutableStateOf(false) }
    var playableLink by remember { mutableStateOf<String?>(null) }

    // Initialize DataStoreManager
    LaunchedEffect(Unit) {
        DataStoreManager.init(context)
    }

    // Handle source that needs Firebase lookup
//    LaunchedEffect(source) {
//        if (source.startsWith("tt")) {
//            FirebaseManager.getMovieById(source) { movie ->
//                val links = movie?.playableLinks ?: emptyList()
//                if (links.isNotEmpty()) {
//                    playableLink = links[0]
//                } else {
//                    // Handle case where no links are available
//                    Log.e("LVideoPlayer", "No playable links found for movie $source")
//                }
//            }
//        } else {
//            playableLink = source
//        }
//    }
    LaunchedEffect(source) {
        if (source.startsWith("tt")) {
            FirebaseManager.getMovieById(source) { movie ->
                val links = movie?.playableLinks ?: emptyList()

                // Launch coroutine to find first valid link
                if (links.isNotEmpty()) {
                    kotlinx.coroutines.GlobalScope.launch {
                        val workingLink = findFirstWorkingLink(links)
                        if (workingLink != null) {
                            playableLink = workingLink
                        } else {
                            Log.e("LVideoPlayer", "No valid links found for movie $source")
                        }
                    }
                } else {
                    Log.e("LVideoPlayer", "No playable links found for movie $source")
                }
            }
        } else {
            playableLink = source
        }
    }

    // Initialize player when we have a valid link and layout
    LaunchedEffect(playableLink, videoLayout) {
        if (playableLink != null && videoLayout != null && !isInitialized) {
            VideoPlayerManager.initialize(context)
            VideoPlayerManager.mediaPlayer.attachViews(videoLayout!!, null, false, false)
            VideoPlayerManager.setMedia(context, playableLink!!)
            VideoPlayerManager.play()
            isInitialized = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isInitialized) {
                VideoPlayerManager.mediaPlayer.detachViews()
                VideoPlayerManager.stop()
                VideoPlayerManager.release()
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                VLCVideoLayout(ctx).apply {
                    keepScreenOn = true
                    videoLayout = this
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (isInitialized) {
            VideoPlayerUi(navController)

            // Restore position if this is the same video
            LaunchedEffect(Unit) {
                if (DataStoreManager.getVideoUri() == playableLink) {
                    DataStoreManager.getVideoDuration()?.let { VideoPlayerManager.seekTo(it) }
                }
            }

            // Save video info and track position
            LaunchedEffect(Unit) {
                playableLink?.let { DataStoreManager.saveVideo(it) }
                while (true) {
                    delay(1000)
                    DataStoreManager.saveDurationData(VideoPlayerManager.getCurrentTime())
                }
            }
        }
    }
}
suspend fun isLinkValid(link: String): Boolean {
    return try {
        val url = java.net.URL(link)
        val connection = url.openConnection()
        connection.connectTimeout = 10000
        connection.getInputStream().close()
        true
    } catch (e: Exception) {
        false
    }
}
suspend fun findFirstWorkingLink(links: List<String>): String? {
    for (link in links) {
        if (isLinkValid(link)) return link
    }
    return null
}
