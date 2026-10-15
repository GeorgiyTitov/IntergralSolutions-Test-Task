package com.georgiytitov.movieexplorer.domain.model

data class Movie(
    val id: Long,
    val title: String,
    val posterPath: String?,
    val overview: String,
    val releaseDate: String,
    val rating: Double,
) {
    val year: String get() = releaseDate.take(4)
    val posterUrl: String? get() = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
}
