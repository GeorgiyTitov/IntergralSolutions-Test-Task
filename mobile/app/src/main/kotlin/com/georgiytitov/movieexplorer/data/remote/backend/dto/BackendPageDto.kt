package com.georgiytitov.movieexplorer.data.remote.backend.dto

data class BackendPageDto(
    val page: Int,
    val totalPages: Int,
    val movies: List<BackendMovieDto>,
)
