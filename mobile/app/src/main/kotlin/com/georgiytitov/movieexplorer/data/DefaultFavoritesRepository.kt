package com.georgiytitov.movieexplorer.data

import com.georgiytitov.movieexplorer.data.local.MovieDao
import com.georgiytitov.movieexplorer.data.local.MovieEntity
import com.georgiytitov.movieexplorer.data.local.toDomain
import com.georgiytitov.movieexplorer.data.local.toEntity
import com.georgiytitov.movieexplorer.data.remote.backend.BackendApi
import com.georgiytitov.movieexplorer.data.remote.backend.dto.AddFavoriteDto
import com.georgiytitov.movieexplorer.data.remote.backend.dto.BackendMovieDto
import com.georgiytitov.movieexplorer.data.remote.network.NetworkMonitor
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.repository.FavoritesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class DefaultFavoritesRepository(
    private val dao: MovieDao,
    private val backend: BackendApi,
    private val userId: String,
    private val network: NetworkMonitor,
    private val scope: CoroutineScope,
) : FavoritesRepository {
    private val synchronization = Mutex()
    private val mutableSyncFailed = MutableStateFlow(false)
    override val syncFailed = mutableSyncFailed.asStateFlow()

    override val favorites = dao.observeFavorites().map { movies ->
        movies.map(MovieEntity::toDomain)
    }

    override suspend fun toggleFavorite(movie: Movie) {
        dao.toggleFavorite(movie.toEntity())
        scope.launch { synchronize() }
    }

    override suspend fun synchronize() {
        synchronization.withLock {
            if (!network.online.value) {
                mutableSyncFailed.value = true
                return@withLock
            }
            try {
                val localIds = dao.favoriteIds().toSet()
                val remoteIds = backend.favorites(userId).map(BackendMovieDto::id).toSet()
                for (id in localIds - remoteIds) {
                    backend.addFavorite(userId, AddFavoriteDto(id))
                }
                for (id in remoteIds - localIds) {
                    backend.removeFavorite(userId, id)
                }
                mutableSyncFailed.value = false
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                mutableSyncFailed.value = true
            }
        }
    }
}
