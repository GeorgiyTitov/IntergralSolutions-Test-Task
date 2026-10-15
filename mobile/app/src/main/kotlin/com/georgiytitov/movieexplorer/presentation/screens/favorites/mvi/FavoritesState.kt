package com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi

import androidx.annotation.StringRes
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesSyncStatus.IDLE

data class FavoritesState(
    val movies: List<Movie> = emptyList(),
    val loading: Boolean = true,
    @StringRes val error: Int? = null,
    val syncStatus: FavoritesSyncStatus = IDLE,
)
