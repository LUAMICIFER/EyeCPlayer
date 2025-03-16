package com.example.eyecplayer

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore

fun getAllVideos(folderPath:String ,context: Context,int: Int): List<MediaFile>{ //MediaFile datatype hai
    val mediaFiles =  mutableListOf<MediaFile>()
    val queryUri = if(Build.VERSION.SDK_INT>=29){
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)//specific video ke liye ho gaya hai
    }else MediaStore.Video.Media.EXTERNAL_CONTENT_URI  // we have just created and uri for the query we will give to the system below
    val projection = arrayOf(
        MediaStore.Video.Media._ID,    // yahan pr MediaStore.Video kar denge to videos ka projection banega warna sara files ka hoga
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION
    ) // we have created projecion for the query
    val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?" // Use LIKE for partial matches
    } else {
        "${MediaStore.Video.Media.DATA} LIKE ?"
    }
    val selectionArgs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf("$folderPath%") // Use wildcard for partial match
    } else {
        arrayOf("$folderPath/%") // Ensure subfolder matching for older versions
    }
    val sortOrder = when (int) {
        0 -> "${MediaStore.Video.Media.DATE_MODIFIED} DESC" // Newest First
        1 -> "${MediaStore.Video.Media.DATE_MODIFIED} ASC"  // Oldest First
        2 -> "${MediaStore.Video.Media.DISPLAY_NAME} ASC"   // Name A-Z
        3 -> "${MediaStore.Video.Media.DISPLAY_NAME} DESC"  // Name Z-A
        4 -> "${MediaStore.Video.Media.DURATION} ASC"       // Shortest Video First
        5 -> "${MediaStore.Video.Media.DURATION} DESC"      // Longest Video First
        else -> "${MediaStore.Video.Media.DATE_MODIFIED} DESC" // Default: Newest First
    }


    context.contentResolver.query(queryUri,projection,selection,selectionArgs,sortOrder)?.use{cursor->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
        while (cursor.moveToNext()){
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            val duration = cursor.getLong(durationColumn)
                val contentUri  = ContentUris.withAppendedId(queryUri,id)
                mediaFiles.add(
                    MediaFile(
                        uri = contentUri,
                        name = name,
                        duration = duration,
                    )
                )


        }
    }
    return mediaFiles.toList()
}
