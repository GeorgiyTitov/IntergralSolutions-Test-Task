package com.georgiytitov.movieexplorer.domain.repository

import com.georgiytitov.movieexplorer.domain.model.DataOrigin
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MovieTab
import com.georgiytitov.movieexplorer.domain.model.MoviePage

interface MovieRepository {
    suspend fun movies(tab: MovieTab, page: Int): MoviePage
    suspend fun movie(id: Long): Pair<Movie, DataOrigin>
}
