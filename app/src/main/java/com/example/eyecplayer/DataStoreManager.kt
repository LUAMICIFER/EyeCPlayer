package com.example.eyecplayer

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
@SuppressLint("StaticFieldLeak")
object DataStoreManager {
    private lateinit var context: Context

    private val Context.dataStore by preferencesDataStore(name = "video_prefs")

    private val VIDEO_URI_KEY = stringPreferencesKey("video_uri")
    private val VIDEO_PROGRESS_KEY = longPreferencesKey("video_progress")

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    suspend fun saveVideo(uri: String) {
        context.dataStore.edit { prefs ->
            prefs[VIDEO_URI_KEY] = uri
//            prefs[VIDEO_PROGRESS_KEY] = progress
        }
    }
    suspend fun saveDurationData(progress: Long) {
        context.dataStore.edit { prefs ->
            prefs[VIDEO_PROGRESS_KEY] = progress
        }
    }
    suspend fun getVideoUri(): String? {
        return context.dataStore.data.map { it[VIDEO_URI_KEY] }.firstOrNull()
    }
    suspend fun getVideoDuration(): Long? {
        return context.dataStore.data.map { it[VIDEO_PROGRESS_KEY] }.firstOrNull()
    }
}
