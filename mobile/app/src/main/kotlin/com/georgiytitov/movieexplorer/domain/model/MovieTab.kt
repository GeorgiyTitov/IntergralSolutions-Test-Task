package com.georgiytitov.movieexplorer.domain.model



enum class MovieTab(val apiValue: String, val label: String) {
    NOW_PLAYING("now_playing", "Now playing"),
    UPCOMING("upcoming", "Upcoming"),
    TOP_RATED("top_rated", "Top rated"),
}
