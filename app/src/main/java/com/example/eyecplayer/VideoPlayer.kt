package com.example.eyecplayer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.AudioManager
import android.net.Uri
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.delay
import java.util.concurrent.Executors
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.max

//@OptIn(UnstableApi::class)
@SuppressLint("DefaultLocale")
@Composable
fun VideoPlayer(navController: NavController, source: String) {
    val context = LocalContext.current
    var userDetection by remember { mutableStateOf(false) }
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    var currentVolume by remember { mutableStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) }

    val exoplayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val uri = Uri.parse(source)
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
        }
    }

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
                            onDoubleTap = { exoplayer.seekTo(exoplayer.currentPosition - 10000) }
                        )
                    }

            ) { /* Empty for gesture detection */

            }

            // Right Side: Seek Forward + Adjust Volume on Vertical Drag
            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { controlVisibility = !controlVisibility },
                            onDoubleTap = { exoplayer.seekTo(exoplayer.currentPosition + 10000) }
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
                        .background(Color.Black.copy(alpha = 0.2f)),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = "back",
                            tint = Color.White,
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
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    if (userDetection && exoplayer.isPlaying) {
                        CameraAnalysis(context, exoplayer)
                    }
                }

                // Bottom Controls (Play/Pause, Seek)
                Spacer(Modifier.weight(1f))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(20.dp)
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    var isPlay by remember { mutableStateOf(exoplayer.isPlaying) }
                    IconButton(onClick = { exoplayer.seekTo(exoplayer.currentPosition - 10000) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_fast_rewind_24),
                            contentDescription = "seek forward",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
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
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    IconButton(onClick = { exoplayer.seekTo(exoplayer.currentPosition + 10000) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_fast_forward_24),
                            contentDescription = "rewind",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    val activity = (context as? Activity)

                    val config = LocalConfiguration.current
                    if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        IconButton(onClick = {
                            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_screen_rotation_24),tint =Color.White,
                                contentDescription = "Rotate Screen"
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_screen_rotation_24), tint = Color.White,
                                contentDescription = "Rotate Screen"
                            )
                        }
                    }

                }
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                exoplayer.release()
            }
        }
    }
}

@Composable
fun CameraAnalysis(context: Context,exoPlayer: ExoPlayer) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FIT_CENTER
            }
            val executor = ContextCompat.getMainExecutor(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            Executors.newSingleThreadExecutor(),
                            ImageAnalysis.Analyzer { imageProxy ->

//                                delay(10000)
                                processImageProxy(imageProxy,context, exoPlayer)
                            }
                        )
                    }

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis)

            }, executor)

            previewView
        }
    )
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
