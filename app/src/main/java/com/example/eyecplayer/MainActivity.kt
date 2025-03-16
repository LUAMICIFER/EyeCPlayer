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
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EyeCPlayerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val uri = intent?.data
                    if (uri != null) {
                        VideoPlayer(uri.toString(),this) // Pass the URI to your player screen
                    } else {
                        PermissionScreen()
                    }
                }
            }
        }
    }


}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun PermissionScreen() {
    val permissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.READ_MEDIA_VIDEO,Manifest.permission.CAMERA)
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
        }
    }

    val multiplePermissionsState = rememberMultiplePermissionsState(permissions = permissions)
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            multiplePermissionsState.allPermissionsGranted -> {
                // Show video list when permission is granted
//                showAllVideo(context)
                ShowAllFoldersContainingVideo(context)
            }
            multiplePermissionsState.shouldShowRationale -> {
                Text("Please grant storage & camera permission to enjoy seamless video playback.")
                Button(onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }) {
                    Text("Grant Permission")
                }
            }
            else -> {
                Text("Storage permission is required to show videos.")
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Open App Settings")
                }
            }
        }
    }
}
