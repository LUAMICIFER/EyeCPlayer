package com.example.eyecplayer

import android.content.Context
import android.os.Build
import android.provider.MediaStore

fun getAllFolderContaingVideo(context : Context): List<String>{
    val folders = mutableListOf<String>()
    val folderSet = mutableSetOf<String>()
    val queryUri = if(Build.VERSION.SDK_INT>=29){
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    }else MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    val projection = if (Build.VERSION.SDK_INT >= 29) {
        arrayOf(MediaStore.Video.Media.RELATIVE_PATH) // Folder Path
    } else {
        arrayOf(MediaStore.Video.Media.DATA) // Full file path
    }
    context.contentResolver.query(queryUri,projection,null,null,null)?.use {cursor ->
        val pathColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RELATIVE_PATH)
        } else {
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA) // DATA is deprecated in API 29+
        }
        while (cursor.moveToNext()) {
            val fullPath = cursor.getString(pathColumn)
            val folderPath = if (Build.VERSION.SDK_INT >= 29) fullPath else fullPath.substringBeforeLast("/") // Extract folder
            folderSet.add(folderPath) // Add folder to set (no duplicates)
        }

    }
    return folderSet.toList()
}