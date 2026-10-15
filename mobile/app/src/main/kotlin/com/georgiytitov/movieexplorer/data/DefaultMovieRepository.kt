package com.georgiytitov.movieexplorer.data

import com.georgiytitov.movieexplorer.data.local.MovieDao
import com.georgiytitov.movieexplorer.data.local.MovieEntity
import com.georgiytitov.movieexplorer.data.local.MoviePageEntity
import com.georgiytitov.movieexplorer.data.local.toDomain
import com.georgiytitov.movieexplorer.data.local.toEntity
import com.georgiytitov.movieexplorer.data.remote.network.NetworkMonitor
import com.georgiytitov.movieexplorer.data.remote.network.PosterCache
import com.georgiytitov.movieexplorer.data.remote.source.MovieDataSource
import com.georgiytitov.movieexplorer.domain.model.DataOrigin
import com.georgiytitov.movieexplorer.domain.model.DataOrigin.BACKEND
import com.georgiytitov.movieexplorer.domain.model.DataOrigin.CACHE
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MoviePage
import com.georgiytitov.movieexplorer.domain.model.MoviePage.Companion.FIRST_PAGE
import com.georgiytitov.movieexplorer.domain.model.MovieTab
import com.georgiytitov.movieexplorer.domain.repository.MovieRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

internal class DefaultMovieRepository(
    private val remote: MovieDataSource,
    private val dao: MovieDao,
    private val network: NetworkMonitor,
    private val posters: PosterCache,
) : MovieRepository {
    private val mutableOrigin = MutableStateFlow(BACKEND)
    override val origin = mutableOrigin.asStateFlow()

    override suspend fun cachedMovies(
        tab: MovieTab,
        page: Int,
    ): MoviePage? {
        val saved = dao.page(tab.apiValue, page) ?: return null
        val ids = saved.movieIds
            .split(MOVIE_ID_SEPARATOR)
            .mapNotNull(String::toLongOrNull)
        val movies = dao
            .movies(ids)
            .associateBy(MovieEntity::id)
        return MoviePage(
            saved.page,
            saved.totalPages,
            ids.mapNotNull { movies[it]?.toDomain() },
            CACHE,
        )
    }

    override suspend fun cachedMovie(id: Long) = dao.movie(id)?.toDomain()

    override suspend fun movies(
        tab: MovieTab,
        page: Int,
    ): MoviePage {
        try {
            val result = fetchMovies(tab = tab, page = page)
            mutableOrigin.value = result.origin
            return result
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            mutableOrigin.value = CACHE
            return cachedMovies(tab, page) ?: throw e
        }
    }

    suspend fun prepareOfflineCache() {
        coroutineScope {
            MovieTab.entries.forEach { tab -> launch { cacheInitialPages(tab = tab) } }
        }
    }

    private suspend fun cacheInitialPages(tab: MovieTab) {
        for (page in FIRST_PAGE..INITIAL_CACHED_PAGES) {
            try {
                val result = fetchMovies(tab = tab, page = page)
                if (page >= result.totalPages) {
                    return
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                return
            }
        }
    }

    private suspend fun fetchMovies(
        tab: MovieTab,
        page: Int,
    ): MoviePage {
        if (!network.online.value) {
            throw IOException(OFFLINE_ERROR)
        }
        val result = remote.movies(tab, page)
        dao.cache(
            MoviePageEntity(
                tab.apiValue,
                result.page,
                result.totalPages,
                result.movies.joinToString(MOVIE_ID_SEPARATOR) { it.id.toString() },
            ),
            result.movies.map(Movie::toEntity),
        )
        posters.store(movies = result.movies)
        return result
    }

    override suspend fun movie(id: Long): Pair<Movie, DataOrigin> {
        try {
            if (!network.online.value) {
                throw IOException(OFFLINE_ERROR)
            }
            val result = remote.movie(id)
            dao.putMovie(result.first.toEntity())
            posters.store(movies = listOf(result.first))
            mutableOrigin.value = result.second
            return result
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            mutableOrigin.value = CACHE
            return (cachedMovie(id) ?: throw e) to CACHE
        }
    }

    private companion object {
        const val MOVIE_ID_SEPARATOR = ","
        const val OFFLINE_ERROR = "Offline"
        const val INITIAL_CACHED_PAGES = 3
    }
}
