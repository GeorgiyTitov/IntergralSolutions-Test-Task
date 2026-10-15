package com.georgiytitov.movieexplorer.domain.model

data class Movie(
    val id: Long,
    val title: String,
    val posterPath: String?,
    val overview: String,
    val releaseDate: String,
    val rating: Double,
) {
    val year: String get() = releaseDate.take(YEAR_LENGTH)
    val posterUrl: String? get() = posterPath?.let { "$POSTER_BASE_URL$it" }

    private companion object {
        const val YEAR_LENGTH = 4
        const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500"
    }
}
