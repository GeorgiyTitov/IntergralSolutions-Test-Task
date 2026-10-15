package com.georgiytitov.movieexplorer.data.remote.tmdb

import com.georgiytitov.movieexplorer.data.remote.source.MovieDataSource
import com.georgiytitov.movieexplorer.data.remote.tmdb.dto.TmdbMovieDto
import com.georgiytitov.movieexplorer.domain.model.DataOrigin.TMDB
import com.georgiytitov.movieexplorer.domain.model.MoviePage
import com.georgiytitov.movieexplorer.domain.model.MovieTab

internal class TmdbDataSource(private val api: TmdbApi) : MovieDataSource {
    override suspend fun movies(
        tab: MovieTab,
        page: Int,
    ): MoviePage {
        val response = api.movies(tab.apiValue, page)
        return MoviePage(
            response.page,
            response.totalPages.coerceAtMost(MAX_PAGE),
            response.results.orEmpty().map(TmdbMovieDto::toDomain),
            TMDB,
        )
    }

    override suspend fun movie(id: Long) = api.movie(id).toDomain() to TMDB

    private companion object {
        const val MAX_PAGE = 500
    }
}
