package com.georgiytitov.movieexplorer.domain.repository

import com.georgiytitov.movieexplorer.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FavoritesRepository {
    val favorites: Flow<List<Movie>>
    val syncFailed: StateFlow<Boolean>

    suspend fun synchronize()

    suspend fun toggleFavorite(movie: Movie)
}
