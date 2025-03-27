package com.example.eyecplayer

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eyecplayer.ui.theme.*
import com.valentinilk.shimmer.shimmer

//import com.example.eyecplayer.ui.theme.PrimaryDark50

@Composable
fun ShowAllFoldersContainingVideo(navController: NavController){
    val context = LocalContext.current
    val folders = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        folders.clear()
        folders.addAll(getAllFolderContaingVideo(context))
        isLoading =false
    }

    Column(
        Modifier
            .background(if (isSystemInDarkTheme()) Color.Black else Color.White)
            .padding(16.dp)) {
        Text(text = "Folders", style = MaterialTheme.typography.displayLarge)

        LazyColumn( verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if(isLoading){
                items(9){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .shimmer()
                            .graphicsLayer {
                                shadowElevation = 8.dp.toPx()
                                shape = RoundedCornerShape(8.dp)
                            }
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSystemInDarkTheme()) DarkGray2 else White)
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(65.dp)
                                .background(Color.Gray, shape = RoundedCornerShape(0.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .height(20.dp)
                                .fillMaxWidth(0.6f)
                                .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                        )
                    }
                }

            }else {
                items(folders) { folder ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                shadowElevation = 8.dp.toPx()
                                shape = RoundedCornerShape(8.dp)
                            }
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                color = if (isSystemInDarkTheme()) DarkGray2
                                else White
                            )
                            .padding(8.dp)
                            .clickable {
                                navController.navigate(
                                    Routes.Videos + "/${
                                        (Uri.encode(folder))
                                    }"
                                )
                            }//If folder contains special characters like spaces, slashes (/), question marks (?), or ampersands (&), they might break the navigation route. Encoding replaces these characters with a safe format.
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_folder_24),
                            contentDescription = "seek forward",
                            tint = PrimaryRed,
                            modifier = Modifier.size(65.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp)) // Spacing between icon and text
                        Text(
                            text = folder,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSystemInDarkTheme()) White else DarkGray1
                        )
                    }
                }
            }
        }
    }
}