package com.georgiytitov.movieexplorer.presentation.favorites

import com.georgiytitov.movieexplorer.domain.model.Movie

data class FavoritesState(
    val movies: List<Movie> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
)
