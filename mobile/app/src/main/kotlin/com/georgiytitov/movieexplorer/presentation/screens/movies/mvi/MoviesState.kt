package com.georgiytitov.movieexplorer.presentation.screens.movies.mvi

import androidx.annotation.StringRes
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MoviePage.Companion.FIRST_PAGE
import com.georgiytitov.movieexplorer.domain.model.MovieTab
import com.georgiytitov.movieexplorer.domain.model.MovieTab.NOW_PLAYING

data class MoviesState(
    val tab: MovieTab = NOW_PLAYING,
    val movies: List<Movie> = emptyList(),
    val loading: Boolean = true,
    val loadingMore: Boolean = false,
    @StringRes val error: Int? = null,
    val nextPage: Int = FIRST_PAGE,
    val totalPages: Int = Int.MAX_VALUE,
)
