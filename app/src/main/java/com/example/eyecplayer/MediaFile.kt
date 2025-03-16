package com.example.eyecplayer

import android.net.Uri
import java.time.Duration

data class MediaFile(
    val uri : Uri,
    val name : String,
    val duration: Long
)
