package com.example.eyecplayer

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.eyecplayer.ui.theme.EyeCPlayerTheme
import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eyecplayer.online.HomeScreen
import com.example.eyecplayer.vp.LVideoPlayer
import com.google.accompanist.permissions.*
import kotlin.time.Duration
val time = 500 // for the animation of navigation
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EyeCPlayerTheme {
                DataStoreManager.init(this)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val uri = intent?.data
                    if (uri != null) {
                        NavHost(navController = navController, startDestination = Routes.player + "/${(Uri.encode(uri.toString()))}") {
                            composable(Routes.player + "/{videoUri}") { backStackEntry ->
                                val videoUri = backStackEntry.arguments?.getString("videoUri")
//                                VideoPlayer(navController, videoUri ?: "/")
                                LVideoPlayer(navController=navController, source =  videoUri ?: "/")

                            }
                        }
                    } else {


                        NavHost(navController = navController, startDestination = Routes.permission,
                            enterTransition={
                            fadeIn(animationSpec = tween(time))+slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left, tween(time)
                            )
                                            },
                        exitTransition={
                            fadeOut(animationSpec = tween(time))+slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down,
                                tween(time)
                            )
                                       },
                        popEnterTransition = {
                            fadeIn(animationSpec = tween(time))+slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right, tween(time)
                            )
                                            },
                        popExitTransition = {
                            fadeOut(animationSpec = tween(time))+slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up,
                                tween(time)
                            )
                        }
                            ) {
                            composable(Routes.permission) { PermissionScreen(navController) }
                            composable(Routes.Folders) { ShowAllFoldersContainingVideo(navController) }
                            composable(Routes.Videos + "/{selectedFolder}") {
                                val selectedFolder = it.arguments?.getString("selectedFolder")
                                showAllVideoOfFolder(navController, selectedFolder ?: "/")
                            }
                            composable(Routes.player + "/{videoUri}") {
                                val videoUri = it.arguments?.getString("videoUri")
//                                VideoPlayer(navController, videoUri ?: "/")
//                                val uri = Uri.parse(Uri.decode(videoUri))
                                LVideoPlayer(navController=navController, source =  videoUri ?: "/")

                            }
                            composable (Routes.online){
                                HomeScreen(navController)
                            }
                        }
                    }
                }
            }
        }
    }


}
@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun PermissionScreen(navController: NavController) {
    val permissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.CAMERA
            )
        } else {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }
    }

    val multiplePermissionsState = rememberMultiplePermissionsState(permissions = permissions)
    val context = LocalContext.current
    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(multiplePermissionsState.allPermissionsGranted) {
        if (multiplePermissionsState.allPermissionsGranted && !hasNavigated) {
            hasNavigated = true
            navController.navigate(Routes.Folders) {
                popUpTo(Routes.permission) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            multiplePermissionsState.allPermissionsGranted -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
//                Text("Permission granted. Redirecting...")
            }

            multiplePermissionsState.permissions.any { it.status.shouldShowRationale } -> {
                Text("Please grant storage & camera permissions to enjoy the app.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    multiplePermissionsState.launchMultiplePermissionRequest()
                }) {
                    Text("Grant Permission")
                }
            }

            else -> {
                Text("Permissions are denied. Please enable them in App Settings.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "Unable to open settings", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Open App Settings")
                }
            }
        }
    }
}
