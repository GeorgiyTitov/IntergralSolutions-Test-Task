package com.georgiytitov.movieexplorer.data

import com.georgiytitov.movieexplorer.data.remote.MovieDataSource
import com.georgiytitov.movieexplorer.domain.model.DataOrigin
import com.georgiytitov.movieexplorer.domain.model.MovieTab
import com.georgiytitov.movieexplorer.domain.repository.MovieRepository

class DefaultMovieRepository(private val remote: MovieDataSource) : MovieRepository {
    override suspend fun movies(tab: MovieTab, page: Int) = remote.movies(tab, page)
    override suspend fun movie(id: Long) = remote.movie(id) to DataOrigin.TMDB
}
