package com.georgiytitov.movieexplorer.data.remote

import com.georgiytitov.movieexplorer.domain.model.DataOrigin
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MoviePage
import com.georgiytitov.movieexplorer.domain.model.MovieTab
import kotlinx.coroutines.CancellationException

class FallbackMovieDataSource(
    private val primary: MovieDataSource,
    private val fallback: MovieDataSource,
) : MovieDataSource {
    override suspend fun movies(
        tab: MovieTab,
        page: Int,
    ): MoviePage {
        return try {
            primary.movies(tab, page)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            fallback.movies(tab, page)
        }
    }

    override suspend fun movie(id: Long): Pair<Movie, DataOrigin> {
        return try {
            primary.movie(id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            fallback.movie(id)
        }
    }
}
