package com.georgiytitov.movieexplorer.domain.repository

import com.georgiytitov.movieexplorer.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    val favorites: Flow<List<Movie>>

    suspend fun synchronize()

    suspend fun setFavorite(
        movie: Movie,
        favorite: Boolean,
    )
}
