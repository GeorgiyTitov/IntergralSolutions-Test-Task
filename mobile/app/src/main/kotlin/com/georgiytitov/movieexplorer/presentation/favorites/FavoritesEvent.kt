package com.georgiytitov.movieexplorer.presentation.favorites

import com.georgiytitov.movieexplorer.domain.model.Movie

sealed interface FavoritesEvent {
    data class Toggle(
        val movie: Movie,
    ) : FavoritesEvent

    data object DismissError : FavoritesEvent

    data class OpenMovie(
        val movieId: Long,
    ) : FavoritesEvent
}
