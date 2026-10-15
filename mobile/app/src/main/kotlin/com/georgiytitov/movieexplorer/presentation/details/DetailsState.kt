package com.georgiytitov.movieexplorer.presentation.details

import com.georgiytitov.movieexplorer.domain.model.Movie

data class DetailsState(val movie: Movie? = null, val loading: Boolean = true, val error: String? = null)
