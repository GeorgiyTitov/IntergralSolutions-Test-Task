package com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi

import com.georgiytitov.movieexplorer.domain.model.Movie

sealed interface FavoritesEvent {
    data class Toggle(val movie: Movie) : FavoritesEvent

    data object DismissError : FavoritesEvent

    data object DismissSyncStatus : FavoritesEvent

    data object RetrySync : FavoritesEvent

    data class OpenMovie(val movieId: Long) : FavoritesEvent
}
