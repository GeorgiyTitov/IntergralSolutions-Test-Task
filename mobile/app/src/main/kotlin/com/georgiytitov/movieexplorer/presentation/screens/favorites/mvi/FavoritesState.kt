package com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi

import androidx.annotation.StringRes
import com.georgiytitov.movieexplorer.domain.model.Movie

data class FavoritesState(
    val movies: List<Movie> = emptyList(),
    val loading: Boolean = true,
    @StringRes val error: Int? = null,
    val syncFailed: Boolean = false,
)
