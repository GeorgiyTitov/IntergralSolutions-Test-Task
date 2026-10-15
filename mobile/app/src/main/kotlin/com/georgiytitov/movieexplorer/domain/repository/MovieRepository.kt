package com.georgiytitov.movieexplorer.domain.repository

import com.georgiytitov.movieexplorer.domain.model.DataOrigin
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MoviePage
import com.georgiytitov.movieexplorer.domain.model.MovieTab
import kotlinx.coroutines.flow.StateFlow

interface MovieRepository {
    val origin: StateFlow<DataOrigin>

    suspend fun movies(
        tab: MovieTab,
        page: Int,
    ): MoviePage

    suspend fun movie(id: Long): Pair<Movie, DataOrigin>

    suspend fun cachedMovies(
        tab: MovieTab,
        page: Int,
    ): MoviePage?

    suspend fun cachedMovie(id: Long): Movie?
}
