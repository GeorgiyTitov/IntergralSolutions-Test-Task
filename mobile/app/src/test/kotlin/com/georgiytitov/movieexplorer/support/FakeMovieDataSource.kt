package com.georgiytitov.movieexplorer.support

import com.georgiytitov.movieexplorer.data.remote.source.MovieDataSource
import com.georgiytitov.movieexplorer.domain.model.DataOrigin
import com.georgiytitov.movieexplorer.domain.model.DataOrigin.BACKEND
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MoviePage
import com.georgiytitov.movieexplorer.domain.model.MovieTab

internal class FakeMovieDataSource : MovieDataSource {
    val pageRequests = mutableListOf<Pair<MovieTab, Int>>()
    val detailRequests = mutableListOf<Long>()
    var loadPage: suspend (MovieTab, Int) -> MoviePage = { _, page ->
        TestMovies.page(number = page)
    }
    var loadMovie: suspend (Long) -> Pair<Movie, DataOrigin> = { TestMovies.first to BACKEND }

    override suspend fun movies(
        tab: MovieTab,
        page: Int,
    ): MoviePage {
        pageRequests += tab to page
        return loadPage(tab, page)
    }

    override suspend fun movie(id: Long): Pair<Movie, DataOrigin> {
        detailRequests += id
        return loadMovie(id)
    }
}
