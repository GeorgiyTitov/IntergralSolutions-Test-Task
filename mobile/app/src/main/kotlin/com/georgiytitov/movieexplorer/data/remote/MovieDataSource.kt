package com.georgiytitov.movieexplorer.data.remote

import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MoviePage
import com.georgiytitov.movieexplorer.domain.model.MovieTab

interface MovieDataSource {
    suspend fun movies(
        tab: MovieTab,
        page: Int,
    ): MoviePage

    suspend fun movie(id: Long): Movie
}
