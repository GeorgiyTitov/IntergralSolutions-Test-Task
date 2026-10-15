package com.georgiytitov.movieexplorer.data.remote.backend

import com.georgiytitov.movieexplorer.data.remote.MovieDataSource
import com.georgiytitov.movieexplorer.domain.model.DataOrigin
import com.georgiytitov.movieexplorer.domain.model.MoviePage
import com.georgiytitov.movieexplorer.domain.model.MovieTab

class BackendDataSource(
    private val api: BackendApi,
) : MovieDataSource {
    override suspend fun movies(
        tab: MovieTab,
        page: Int,
    ): MoviePage {
        val response = api.movies(tab.apiValue, page)
        return MoviePage(
            page = response.page,
            totalPages = response.totalPages,
            movies = response.movies.map { it.toDomain() },
            origin = DataOrigin.BACKEND,
        )
    }

    override suspend fun movie(id: Long) = api.movie(id).toDomain() to DataOrigin.BACKEND
}
