package com.georgiytitov.movieexplorer.data.remote.source

import com.georgiytitov.movieexplorer.domain.model.DataOrigin
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MoviePage
import com.georgiytitov.movieexplorer.domain.model.MovieTab

internal interface MovieDataSource {
    suspend fun movies(
        tab: MovieTab,
        page: Int,
    ): MoviePage

    suspend fun movie(id: Long): Pair<Movie, DataOrigin>
}
