package com.georgiytitov.movieexplorer.data

import com.georgiytitov.movieexplorer.data.local.MovieDao
import com.georgiytitov.movieexplorer.data.local.toDomain
import com.georgiytitov.movieexplorer.data.local.toEntity
import com.georgiytitov.movieexplorer.data.remote.NetworkMonitor
import com.georgiytitov.movieexplorer.data.remote.backend.BackendApi
import com.georgiytitov.movieexplorer.data.remote.backend.dto.AddFavoriteDto
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.repository.FavoritesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_NOT_FOUND

class DefaultFavoritesRepository(
    private val dao: MovieDao,
    private val backend: BackendApi,
    private val userId: String,
    private val network: NetworkMonitor,
    private val scope: CoroutineScope,
) : FavoritesRepository {
    private val synchronization = Mutex()
    override val favorites = dao.observeFavorites().map { movies -> movies.map { it.toDomain() } }

    override suspend fun setFavorite(
        movie: Movie,
        favorite: Boolean,
    ) {
        dao.setFavorite(movie.toEntity(), favorite)
        scope.launch { synchronize() }
    }

    override suspend fun synchronize() {
        if (!network.online.value) {
            return
        }
        synchronization.withLock {
            for (change in dao.pendingFavorites()) {
                try {
                    if (change.desired) {
                        try {
                            backend.addFavorite(userId, AddFavoriteDto(change.movieId))
                        } catch (e: HttpException) {
                            if (e.code() != HTTP_BAD_REQUEST ||
                                backend.favorites(userId).none { it.id == change.movieId }
                            ) {
                                throw e
                            }
                        }
                    } else {
                        try {
                            backend.removeFavorite(userId, change.movieId)
                        } catch (e: HttpException) {
                            if (e.code() != HTTP_NOT_FOUND) {
                                throw e
                            }
                        }
                    }
                    dao.acknowledgeFavorite(change.movieId, change.modifiedAt, change.desired)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // Local state is already durable. Retry pending changes on the next connection.
                    break
                }
            }
        }
    }
}
