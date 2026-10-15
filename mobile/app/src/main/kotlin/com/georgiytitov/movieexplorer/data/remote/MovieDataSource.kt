package com.georgiytitov.movieexplorer.data.remote

import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MovieTab
import com.georgiytitov.movieexplorer.domain.model.MoviePage

interface MovieDataSource {
    suspend fun movies(tab: MovieTab, page: Int): MoviePage
    suspend fun movie(id: Long): Movie
}
