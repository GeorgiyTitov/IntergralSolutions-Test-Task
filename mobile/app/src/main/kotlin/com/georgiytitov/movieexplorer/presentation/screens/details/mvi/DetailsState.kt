package com.georgiytitov.movieexplorer.presentation.screens.details.mvi

import androidx.annotation.StringRes
import com.georgiytitov.movieexplorer.domain.model.Movie

data class DetailsState(
    val movie: Movie? = null,
    val loading: Boolean = true,
    @StringRes val error: Int? = null,
)
