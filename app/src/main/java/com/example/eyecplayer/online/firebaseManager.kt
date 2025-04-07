package com.example.eyecplayer.online

import android.util.Log
import androidx.media3.common.util.Log.e
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
object FirebaseManager {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getRecentMovies(): List<Movie> {
        return try {
            val snapshot = db.collection("movies")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()

            snapshot.documents.map { document ->
                Movie(
                    imdbId = document.getString("imdbId") ?: "",
                    title = document.getString("title") ?: "",
                    releaseDate = document.getString("releaseDate") ?: "",
                    genres = document.get("genres") as? List<String> ?: emptyList(),
                    playableLinks = document.get("playableLinks") as? List<String> ?: emptyList(),
                    thumbnailLink = document.getString("thumbnailLink") ?: "",
                    createdAt = document.getLong("createdAt") ?: System.currentTimeMillis()
                )
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching recent movies", e)
            emptyList() // Return an empty list in case of an error
        }
    }
    fun getMoviesByGenre(genre: String, onResult: (List<Movie>) -> Unit) {
        db.collection("movies")
            .whereArrayContains("genres", genre)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val moviesList = result.map { document ->
                    document.toObject(Movie::class.java)
                }
                onResult(moviesList)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
                onResult(emptyList()) // Return an empty list on failure
            }
    }
    fun getMovieById(movieId: String, onResult: (Movie?) -> Unit) {
        db.collection("movies").document(movieId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val movie = document.toObject(Movie::class.java)
                    onResult(movie)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }


}
