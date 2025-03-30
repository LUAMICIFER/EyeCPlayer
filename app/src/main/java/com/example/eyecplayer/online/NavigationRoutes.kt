package com.example.eyecplayer.online

sealed class Screen(val route: String) {
    object Home : Screen("HOME")
    object Category : Screen("CATEGORY")
    object Watchlist : Screen("WATCHLIST")
    object Profile : Screen("PROFILE")
}