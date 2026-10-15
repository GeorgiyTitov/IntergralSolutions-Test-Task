package com.georgiytitov.movieexplorer.data.remote.backend.dto

internal data class BackendMovieDto(
    val id: Long,
    val title: String?,
    val posterPath: String?,
    val overview: String?,
    val releaseDate: String?,
    val rating: Double,
)
