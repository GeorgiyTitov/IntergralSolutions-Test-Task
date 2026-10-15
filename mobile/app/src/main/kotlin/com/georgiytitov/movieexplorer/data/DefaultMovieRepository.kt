package com.georgiytitov.movieexplorer.data

import com.georgiytitov.movieexplorer.data.local.*
import com.georgiytitov.movieexplorer.data.remote.MovieDataSource
import com.georgiytitov.movieexplorer.data.remote.NetworkMonitor
import com.georgiytitov.movieexplorer.domain.model.DataOrigin
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MoviePage
import com.georgiytitov.movieexplorer.domain.model.MovieTab
import com.georgiytitov.movieexplorer.domain.repository.MovieRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException

class DefaultMovieRepository(
    private val remote: MovieDataSource,
    private val dao: MovieDao,
    private val network: NetworkMonitor,
) : MovieRepository {
    private val mutableOrigin = MutableStateFlow(DataOrigin.BACKEND)
    override val origin = mutableOrigin.asStateFlow()

    override suspend fun cachedMovies(
        tab: MovieTab,
        page: Int,
    ): MoviePage? {
        val saved = dao.page(tab.apiValue, page) ?: return null
        val ids =
            saved.movieIds
                .split(',')
                .mapNotNull(String::toLongOrNull)
        val movies =
            dao
                .movies(ids)
                .associateBy { it.id }
        return MoviePage(
            saved.page,
            saved.totalPages,
            ids.mapNotNull {
                movies[it]?.toDomain()
            },
            DataOrigin.CACHE,
        )
    }

    override suspend fun cachedMovie(id: Long) =
        dao
            .movie(id)
            ?.toDomain()

    override suspend fun movies(
        tab: MovieTab,
        page: Int,
    ): MoviePage {
        try {
            if (!network.online.value) {
                throw IOException("Offline")
            }
            val result = remote.movies(tab, page)
            dao.cache(
                MoviePageEntity(
                    tab.apiValue,
                    result.page,
                    result.totalPages,
                    result.movies.joinToString(
                        ",",
                    ) {
                        it.id.toString()
                    },
                ),
                result.movies.map { it.toEntity() },
            )
            mutableOrigin.value = result.origin
            return result
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            mutableOrigin.value = DataOrigin.CACHE
            return cachedMovies(tab, page) ?: throw e
        }
    }

    override suspend fun movie(id: Long): Pair<Movie, DataOrigin> {
        try {
            if (!network.online.value) {
                throw IOException("Offline")
            }
            val result = remote.movie(id)
            dao.putMovie(result.first.toEntity())
            mutableOrigin.value = result.second
            return result
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            mutableOrigin.value = DataOrigin.CACHE
            return (cachedMovie(id) ?: throw e) to DataOrigin.CACHE
        }
    }
}
