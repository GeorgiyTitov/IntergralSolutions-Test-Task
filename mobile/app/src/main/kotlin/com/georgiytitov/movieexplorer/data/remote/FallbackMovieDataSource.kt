package com.georgiytitov.movieexplorer.data.remote

import com.georgiytitov.movieexplorer.domain.model.MovieTab
import kotlinx.coroutines.CancellationException

class FallbackMovieDataSource(
    private val primary: MovieDataSource,
    private val fallback: MovieDataSource,
) : MovieDataSource {
    override suspend fun movies(
        tab: MovieTab,
        page: Int,
    ) = try {
        primary.movies(tab, page)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        fallback.movies(tab, page)
    }

    override suspend fun movie(id: Long) =
        try {
            primary.movie(id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            fallback.movie(id)
        }
}
