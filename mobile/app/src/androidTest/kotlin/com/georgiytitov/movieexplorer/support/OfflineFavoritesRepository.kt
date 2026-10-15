package com.georgiytitov.movieexplorer.support

import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.repository.FavoritesRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow

class OfflineFavoritesRepository : FavoritesRepository {
    override val favorites = MutableStateFlow(listOf(UiTestMovies.first))
    override val syncFailed = MutableStateFlow(true)
    private var response = CompletableDeferred<Unit>()
    var synchronizationCount = 0
        private set

    override suspend fun synchronize() {
        synchronizationCount++
        response.await()
        syncFailed.value = true
    }

    fun completeAttempt() {
        response.complete(Unit)
        response = CompletableDeferred()
    }

    override suspend fun toggleFavorite(movie: Movie) {
        favorites.value = favorites.value.filterNot { favorite -> favorite.id == movie.id }
    }
}
