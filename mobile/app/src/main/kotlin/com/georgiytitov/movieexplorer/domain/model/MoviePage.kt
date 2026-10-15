package com.georgiytitov.movieexplorer.domain.model



data class MoviePage(
    val page: Int,
    val totalPages: Int,
    val movies: List<Movie>,
    val origin: DataOrigin,
)
