package com.georgiytitov.movieexplorer.support

import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow

class FakeFavoritesRepository : FavoritesRepository {
    override val favorites = MutableStateFlow<List<Movie>>(emptyList())
    override val syncFailed = MutableStateFlow(false)
    var synchronizationCount = 0
    var synchronizeAction: suspend () -> Unit = { syncFailed.value = false }
    var toggleAction: suspend (Movie) -> Unit = {}

    override suspend fun synchronize() {
        synchronizationCount++
        synchronizeAction()
    }

    override suspend fun toggleFavorite(movie: Movie) {
        toggleAction(movie)
    }
}
