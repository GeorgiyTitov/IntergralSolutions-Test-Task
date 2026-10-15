package com.georgiytitov.movieexplorer.presentation.screens.movies.mvi

import com.georgiytitov.movieexplorer.domain.model.MovieTab

sealed interface MoviesEvent {
    data class SelectTab(val tab: MovieTab) : MoviesEvent

    data object Refresh : MoviesEvent

    data object LoadNext : MoviesEvent

    data class OpenMovie(val movieId: Long) : MoviesEvent
}
