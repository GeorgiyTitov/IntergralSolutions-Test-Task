package com.georgiytitov.movieexplorer.data.remote.backend.dto

internal data class BackendPageDto(
    val page: Int,
    val totalPages: Int,
    val movies: List<BackendMovieDto>,
)
