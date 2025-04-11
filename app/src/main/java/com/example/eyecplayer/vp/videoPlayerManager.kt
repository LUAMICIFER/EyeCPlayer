package com.example.eyecplayer.vp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import com.example.eyecplayer.online.FirebaseManager
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer

@SuppressLint("StaticFieldLeak")
object VideoPlayerManager {
    private lateinit var libVLC: LibVLC
    private lateinit var appContext: Context // app context for safe usage
    fun initialize(context: Context) {
        appContext = context.applicationContext
        libVLC = LibVLC(appContext)
        mediaPlayer = MediaPlayer(libVLC)
    }

    lateinit var mediaPlayer: MediaPlayer
//    content:// URIs don’t point to a physical file, so VLC can’t open them unless you give it access via FileDescriptor.
//
//    contentResolver.openAssetFileDescriptor() safely bridges that gap.
fun getPlayableLinks(source: String, onResult: (List<String>) -> Unit) {
    if (source.startsWith("tt")) {
        FirebaseManager.getMovieById(source) { movie ->
            val links = movie?.playableLinks ?: listOf(
                "https://m3u8.wafflehacker.io/m3u8-proxy?url=https%3A%2F%2Fs2.phim1280.tv%2F20231017%2F20MEMVbZ%2Findex.m3u8"
            )
            onResult(links)
        }
    } else {
        onResult(listOf(Uri.decode(source)))
    }
}

//    fun setMedia(context: Context, source: String) {
//        val media =Media(libVLC, source)
//        //        if (source.startsWith("http") || source.startsWith("https")) {
//
////            Media(libVLC, source)
////        }
////        else {
////            val uri = Uri.parse(source)
////            val assetFD = context.contentResolver.openAssetFileDescriptor(uri, "r")
////            Media(libVLC, assetFD!!.fileDescriptor)
////        }
//        mediaPlayer.media = media
//        media.release()
//    }
fun setMedia(context: Context, source: String) {
    val media = if (source.startsWith("http") || source.startsWith("https")) {
        Media(libVLC, Uri.parse(source))
    } else {
        val uri = Uri.parse(source)
        val assetFD = context.contentResolver.openAssetFileDescriptor(uri, "r")
        Media(libVLC, assetFD!!.fileDescriptor)
    }

    mediaPlayer.media = media
    media.release()
}


    fun play() = mediaPlayer.play()
    fun pause() = mediaPlayer.pause()
    fun stop() = mediaPlayer.stop()
    fun isPlaying() = mediaPlayer.isPlaying

    fun release() {
        mediaPlayer.release()
        libVLC.release()
    }

    private val SEEK_INTERVAL = 10_000L

    fun seekForward() {
        val currentTime = mediaPlayer.time
        mediaPlayer.time = currentTime + SEEK_INTERVAL
    }

    fun seekBackward() {
        val currentTime = mediaPlayer.time
        mediaPlayer.time = (currentTime - SEEK_INTERVAL).coerceAtLeast(0)
    }

    fun setPlaybackSpeed(speed: Float) {
        mediaPlayer.rate = speed
    }
    fun getAudioTrack():List<MediaPlayer.TrackDescription>{
        return mediaPlayer.audioTracks?.toList() ?: emptyList()
    }
    fun changeAudioTrack(trackId: Int) {
        mediaPlayer.setAudioTrack(trackId)
    }

    fun getSubtitleTracks(): List<MediaPlayer.TrackDescription> {
        return mediaPlayer.spuTracks?.toList() ?: emptyList()
    }

    fun setSubtitleTrack(trackId: Int) {
        mediaPlayer.spuTrack = trackId
    }
    fun isSubtitleEnabled(): Boolean {
        return mediaPlayer.spuTrack != -1
    }

    fun disableSubtitles() {
        mediaPlayer.spuTrack = -1
    }

    // ✅ Set volume (sync with system volume)
    fun setVolume(percent: Int) {
        val clamped = percent.coerceIn(0, 100)
        mediaPlayer.volume = clamped

        val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val systemVolume = (clamped * maxVolume / 100).coerceIn(0, maxVolume)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, systemVolume, 0)
    }

    // ✅ Get current volume in percent
    fun getVolume(): Int {
        val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        return (current * 100 / max)
    }
    fun getCurrentTime(): Long {
        return mediaPlayer.time
    }

    fun getTotalDuration(): Long {
        return mediaPlayer.length
    }

    fun seekTo(position: Long) {
        mediaPlayer.time = position
    }
    fun isInitialized(): Boolean {
        return ::mediaPlayer.isInitialized
    }
    fun Activity.lockToLandscape() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    fun Activity.lockToPortrait() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
//    fun getRealPathFromURI(context: Context, uri: Uri): String? {
//        val projection = arrayOf(MediaStore.Video.Media.DATA)
//        val cursor = context.contentResolver.query(uri, projection, null, null, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
//                return it.getString(columnIndex)
//            }
//        }
//        return null
//    }

}
