package com.example.eyecplayer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.delay
import java.util.concurrent.Executors
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.navigation.compose.rememberNavController
import com.example.eyecplayer.ui.theme.DarkGray3
import com.example.eyecplayer.ui.theme.DarkGray5
import com.example.eyecplayer.ui.theme.LightGray3
import com.example.eyecplayer.ui.theme.LightGray4
import com.example.eyecplayer.ui.theme.PrimaryRed
import com.example.eyecplayer.ui.theme.White
import com.google.mlkit.vision.face.Face
import java.time.Duration
import java.time.LocalTime

//@OptIn(UnstableApi::class)
@OptIn(UnstableApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("DefaultLocale")
@Composable
fun VideoPlayer(navController: NavController, source: String) {

    val context = LocalContext.current
    var userDetection by remember { mutableStateOf(false) }
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    var currentVolume by remember { mutableStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) }
    var speed by remember { mutableStateOf(1f) }
    val activity = (context as? Activity) // roation aaur screen on rakhne dono ke kaam aaya hai

    // buffer kitna kb hoga uska hai ye first -> kitne load hone ke baad video start hoga ,second-> ek baar me kitna load karke rakhega ,third-> kitna load hoga to dubara chalu ho jayega video chalna ,fourth->
    val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(15000, 300000, 2000, 5000)
        .build()
// using media source is faster than the setMediaItem
    val uri = Uri.parse(source)
    val exoplayer = remember {
        ExoPlayer.Builder(context).setLoadControl(loadControl)
            .setRenderersFactory(DefaultRenderersFactory(context).setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON))
            .build().apply {

//            setWakeMode(C.WAKE_MODE_LOCAL) //optional
            setMediaItem(MediaItem.fromUri(uri))
//            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onPlayerError(error:  androidx.media3.common.PlaybackException) {
                        super.onPlayerError(error)
                        Toast.makeText(context, "Playback Error: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
    // Mathing if the uri source is same then only plays the video with the past duration
    LaunchedEffect(Unit){
        if (DataStoreManager.getVideoUri() == source) {
            DataStoreManager.getVideoDuration()?.let { exoplayer.seekTo(it) }
        }
    }
    LaunchedEffect(Unit) {
        DataStoreManager.saveVideo(source)}

    //for the progress bar it is updating it every half second
    var progress by remember { mutableStateOf(0f) }
    var duration by remember { mutableStateOf(1f) }
//    if()

    var controlVisibility by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = false
                    this.player = exoplayer
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Row(Modifier.fillMaxSize()) {
            // Left Side: Seek Backward on Double Tap
            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { controlVisibility = !controlVisibility },
                            onDoubleTap = { exoplayer.seekTo(exoplayer.currentPosition - 10000) },
//                            onLongPress = {exoplayer.setPlaybackSpeed(2f)}
                            onPress = {
                                speed = 2f
                                exoplayer.setPlaybackSpeed(speed)  // Increase speed on touch
                                tryAwaitRelease() // Wait until the finger is lifted
                                speed = 1f
                                exoplayer.setPlaybackSpeed(speed)  // Reset speed on release
                            }
                        )
                    }

            ) { /* Empty for gesture detection */

            }

            // Right Side: Seek Forward + Adjust Volume on Vertical Drag + incresed speed to 2x for the long press
            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { controlVisibility = !controlVisibility },
                            onDoubleTap = { exoplayer.seekTo(exoplayer.currentPosition + 10000) },
                            onPress = {
                                speed = 2f
                                exoplayer.setPlaybackSpeed(speed)  // Increase speed on touch
                                tryAwaitRelease() // Wait until the finger is lifted
                                speed = 1f
                                exoplayer.setPlaybackSpeed(speed)  // Reset speed on release
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            val stepSize = 1  // Adjust volume step
                            val threshold = 10f // Minimum drag threshold to register change

                            if (dragAmount > threshold) {
                                // Swipe Down (Decrease Volume)
                                val newVolume = (currentVolume - stepSize).coerceIn(0, maxVolume)
                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    newVolume,
                                    AudioManager.FLAG_SHOW_UI
                                )
                                currentVolume = newVolume
                            } else if (dragAmount < -threshold) {
                                // Swipe Up (Increase Volume)
                                val newVolume = (currentVolume + stepSize).coerceIn(0, maxVolume)
                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    newVolume,
                                    AudioManager.FLAG_SHOW_UI
                                )
                                currentVolume = newVolume
                            }
                        }
                    }

            ) {
//                Text(text = "Device Volume: $currentVolume / $maxVolume")
            }
        }

        // UI Controls Visibility Logic
        if (controlVisibility) {
            LaunchedEffect(exoplayer) {
                while (controlVisibility) {
                    progress = exoplayer.currentPosition.toFloat()
                    duration = exoplayer.duration.toFloat().coerceAtLeast(1f)
                    DataStoreManager.saveDurationData(exoplayer.currentPosition.toLong())
                    delay(500) // Update every 500ms
                }
            }
            if (exoplayer.isPlaying) {
                LaunchedEffect(exoplayer.isPlaying) {
                    delay(30000)
                    controlVisibility = false
                }
            }

            Column {
                // Top Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 25.dp, top = 20.dp, end = 25.dp)
                        .background(Color.Black.copy(alpha = 0f)),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = "back",
                            tint = LightGray3,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    IconButton(onClick = { userDetection = !userDetection }) {
                        Icon(
                            painter = painterResource(
                                id = if (userDetection) {
                                    R.drawable.baseline_visibility_24
                                } else {
                                    R.drawable.baseline_visibility_off_24
                                }
                            ),
                            contentDescription = "User Face Detection",
                            tint = LightGray3,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    if (userDetection && exoplayer.isPlaying) {
//                        FaceDetectionToggle(exoplayer,modifier = Modifier.size(40.dp))
                        OptimizedCameraAnalysis(context, exoplayer)

                    }
                    //deepseek
//                    FaceDetectionToggle(exoplayer,modifier = Modifier.size(40.dp))
                }

                // Bottom Controls (Play/Pause, Seek)

                Spacer(Modifier.weight(1f))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0f))
//                        .padding(4.dp)
                        .wrapContentHeight()
                    , horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                    Row(Modifier.wrapContentWidth(Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically){
                        Text(formatDuration(progress.toLong()))
                        Slider(modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(0.8f),
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryRed,
                                activeTrackColor = PrimaryRed,
                                inactiveTrackColor = LightGray4
                            ),
                            value = progress / duration,
                            onValueChange = { newValue ->
                                exoplayer.seekTo((newValue * duration).toLong())
                            }
                        )
                        Text(formatDuration(duration.toLong()))
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        val config = LocalConfiguration.current
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
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
                                Text("Potrait",color = Color.White)

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
                                Text("Landscape",color = Color.White)

                        }
                        var isPlay by remember { mutableStateOf(exoplayer.isPlaying) }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = { exoplayer.seekTo(exoplayer.currentPosition - 10000) }) {

                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_fast_rewind_24),
                                    contentDescription = "rewind",
                                    tint = LightGray3,
                                    modifier = Modifier.size(40.dp)
                                )

                            }
                            Text("Backward", color = Color.White)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            IconButton(onClick = {
                                if (isPlay) {
                                    exoplayer.pause()
                                } else {
                                    exoplayer.play()
                                }
                                isPlay = exoplayer.isPlaying
                            }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (isPlay) {
                                            R.drawable.baseline_pause_24
                                        } else {
                                            R.drawable.baseline_play_arrow_24
                                        }
                                    ),
                                    contentDescription = "seek forward",
                                    tint = LightGray3,
                                    modifier = Modifier.size(40.dp)
                                )

                            }
                            Text(text = "play/pause", color = Color.White)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            IconButton(onClick = { exoplayer.seekTo(exoplayer.currentPosition + 10000) }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_fast_forward_24),
                                    contentDescription = "forward",
                                    tint = LightGray3,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Text("forward", color = Color.White)
                        }




                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            IconButton(onClick = {
                                speed = if (speed >= 2f) 0.5f else (speed + 0.5f)
                                exoplayer.setPlaybackSpeed(speed)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_speed_24),
                                    contentDescription = "speed control",
                                    tint = LightGray3,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Text("$speed", color = Color.White)

                        }

                        }

                    }
                }
            }
        }

    DisposableEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            exoplayer.release()
        }
    }

}


@Composable
fun CameraAnalysis(context: Context, exoPlayer: ExoPlayer) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // Store the start time in remember
    val local = remember { mutableStateOf(LocalTime.now()) }

    var duration by remember { mutableStateOf(Duration.ZERO) }
    val cameraProviderState = remember { mutableStateOf<ProcessCameraProvider?>(null) } //ye state type ka kaam karega aage camera provider ka value ko scope ke bahar use karne ke liye
    LaunchedEffect(Unit) {
        while (true) {
            duration= Duration.between(local.value, LocalTime.now())
            delay(1000) // Update every second
        }
    }
    // Start analysis only if time exceeds 10 minutes

    if (duration.toSeconds() > 10) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FIT_CENTER
                }
                val executor = ContextCompat.getMainExecutor(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    cameraProviderState.value = cameraProvider
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(
                                Executors.newSingleThreadExecutor(),
                                ImageAnalysis.Analyzer { imageProxy ->
                                    processImageProxy(imageProxy, context, exoPlayer)
                                }
                            )
                        }

                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis)

                    // Stop analysis if time exceeds 11 minutes
                    if (duration.toSeconds() > 15) {
                        cameraProvider.unbindAll()
                        local.value = LocalTime.now() // Reset timer
                    }

                }, executor)

                previewView
            }
        )
    }
    LaunchedEffect(duration) {
        if (duration.toSeconds() > 15) {
            cameraProviderState.value?.unbindAll() // Unbind camera properly
            local.value = LocalTime.now() // Reset timer
        }
    }

}


@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(imageProxy: ImageProxy,context: Context, exoPlayer: ExoPlayer) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        val detector = FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()
        )

        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val face = faces.first()

                    val leftEyeOpenProb = face.leftEyeOpenProbability ?: 0f
                    val rightEyeOpenProb = face.rightEyeOpenProbability ?: 0f
                    val smileProb = face.smilingProbability ?: 0f

//                    Log.d("FaceDetection", "Left Eye: $leftEyeOpenProb, Right Eye: $rightEyeOpenProb")

                    if (leftEyeOpenProb < 0.3 && rightEyeOpenProb < 0.3) {
                        exoPlayer.pause()
                        Toast.makeText(context, "hello dono ankhe band hai ya dur ho", Toast.LENGTH_SHORT).show()
                    }
                    if (smileProb>0.7) {
                        exoPlayer.play()

                    }
                } else {
//                    Log.d("FaceDetection", "No face detected")
                    exoPlayer.pause()
                }
            }
            .addOnCompleteListener {
                imageProxy.close() // Important: Close ImageProxy
            }
    } else {
        imageProxy.close()
    }

}
// deepseek logic implementation
//@Composable
//fun FaceDetectionToggle(
//    exoPlayer: ExoPlayer,
//    modifier: Modifier = Modifier
//) {
//    val context = LocalContext.current
//    var userDetection by rememberSaveable { mutableStateOf(false) }
//
//    IconButton(
//        onClick = { userDetection = !userDetection },
//        modifier = modifier.size(40.dp)
//    ) {
//        Icon(
//            painter = painterResource(
//                id = if (userDetection) {
//                    R.drawable.baseline_visibility_24
//                } else {
//                    R.drawable.baseline_visibility_off_24
//                }
//            ),
//            contentDescription = "User Face Detection",
//            tint = Color.White
//        )
//    }
//
//    if (userDetection && exoPlayer.isPlaying) {
//        OptimizedCameraAnalysis(context, exoPlayer)
//    }
//}

@Composable
fun OptimizedCameraAnalysis(context: Context, exoPlayer: ExoPlayer) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    // Detection state management
    var detectionStartTime by remember { mutableStateOf(LocalTime.now()) }
    var isDetectionWindowActive by remember { mutableStateOf(false) }
    var eyesClosedDuration by remember { mutableStateOf(0L) } // in seconds

    // Schedule periodic detection windows
    LaunchedEffect(Unit) {
        while (true) {
            // Wait for 50 seconds (idle period)
            delay(50_000L)

            // Start 10-second detection window
            isDetectionWindowActive = true
            detectionStartTime = LocalTime.now()
            eyesClosedDuration = 0

            // Run detection for 10 seconds
            delay(10_000L)

            // End detection window
            isDetectionWindowActive = false

            // Check if eyes were closed for entire window
            if (eyesClosedDuration >= 10) {
                exoPlayer.pause()
                // Show toast or other notification
            }
        }
    }

    // Camera and analysis lifecycle
    LaunchedEffect(isDetectionWindowActive) {
        if (isDetectionWindowActive) {
            // Start camera only during active detection windows
            val provider = cameraProviderFuture.get()
            cameraProvider = provider

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(
                        Executors.newSingleThreadExecutor(),
                        FaceAnalyzer { face ->
                            handleFaceDetectionResult(face, exoPlayer, context) {
                                eyesClosedDuration++ // Increment closed duration
                            }
                        }
                    )
                }
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("CameraAnalysis", "Failed to bind use cases", e)
            }
        } else {
            // Release camera during idle periods
            cameraProvider?.unbindAll()
            cameraProvider = null
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    // Show camera preview only during detection windows
}

private fun handleFaceDetectionResult(
    face: Face,
    exoPlayer: ExoPlayer,
    context: Context,
    onEyesClosed: () -> Unit
) {
    val leftEyeOpenProb = face.leftEyeOpenProbability ?: 0f
    val rightEyeOpenProb = face.rightEyeOpenProbability ?: 0f
    val smileProb = face.smilingProbability ?: 0f

    when {
        // Both eyes closed
        leftEyeOpenProb < 0.3 && rightEyeOpenProb < 0.3 -> {
            exoPlayer.pause()
            onEyesClosed()
        }
        // Smiling detected
        smileProb > 0.7 -> {
            exoPlayer.play()
        }
        // Eyes open but not smiling - no action
    }
}

private class FaceAnalyzer(
    private val onFaceDetected: (Face) -> Unit
) : ImageAnalysis.Analyzer {
    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
    )

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            detector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        onFaceDetected(faces.first())
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FaceAnalyzer", "Face detection failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
fun formatDuration(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60)) % 60
    val hours = milliseconds / (1000 * 60 * 60)

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
//@RequiresApi(Build.VERSION_CODES.S)
//@Preview(showBackground = true)
//@Composable
//private fun video() {
//    Row{
//        Text(formatDuration(0.toLong()))
//        Slider(modifier = Modifier
//            .weight(1f)
//            .fillMaxWidth(0.8f),
//            colors = SliderDefaults.colors(
//                thumbColor = PrimaryRed,
//                activeTrackColor = PrimaryRed,
//                inactiveTrackColor = LightGray4
//            ),
//            value = 0.3f,
//            onValueChange = { newValue ->
////                exoplayer.seekTo((newValue * duration).toLong())
//            }
//        )
//        Text(formatDuration(0.toLong()))
//    }
//}