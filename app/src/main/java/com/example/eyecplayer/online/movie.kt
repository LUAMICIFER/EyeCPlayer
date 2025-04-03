package com.example.eyecplayer.online

data class Movie(
    val imdbId: String = "",
    val title: String = "",
    val releaseDate: String = "", // New field for original release date
    val genres: List<String> = emptyList(),
    val playableLinks: List<String> = emptyList(),
    val thumbnailLink: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", emptyList(), emptyList(), "", 0L)
}
