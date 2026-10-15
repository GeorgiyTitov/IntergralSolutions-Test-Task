package com.georgiytitov.movieexplorer.data.remote.tmdb

import com.georgiytitov.movieexplorer.data.remote.MovieDataSource
import com.georgiytitov.movieexplorer.domain.model.DataOrigin
import com.georgiytitov.movieexplorer.domain.model.MoviePage
import com.georgiytitov.movieexplorer.domain.model.MovieTab

class TmdbDataSource(
    private val api: TmdbApi,
) : MovieDataSource {
    override suspend fun movies(
        tab: MovieTab,
        page: Int,
    ): MoviePage {
        val response = api.movies(tab.apiValue, page)
        return MoviePage(
            response.page,
            response.totalPages.coerceAtMost(
                500,
            ),
            response.results.orEmpty().map {
                it.toDomain()
            },
            DataOrigin.TMDB,
        )
    }

    override suspend fun movie(id: Long) = api.movie(id).toDomain() to DataOrigin.TMDB
}
