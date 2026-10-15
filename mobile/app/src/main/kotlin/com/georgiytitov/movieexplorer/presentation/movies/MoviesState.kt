package com.georgiytitov.movieexplorer.presentation.movies

import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MovieTab

data class MoviesState(
    val tab: MovieTab = MovieTab.NOW_PLAYING,
    val movies: List<Movie> = emptyList(),
    val loading: Boolean = true,
    val loadingMore: Boolean = false,
    val error: String? = null,
    val nextPage: Int = 1,
    val totalPages: Int = Int.MAX_VALUE,
)
