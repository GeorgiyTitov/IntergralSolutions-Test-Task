package com.georgiytitov.movieexplorer.presentation.navigation

sealed interface NavigationEvent {
    data class OpenMovie(
        val movieId: Long,
    ) : NavigationEvent

    data object Back : NavigationEvent
}
