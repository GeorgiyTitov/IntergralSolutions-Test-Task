package com.georgiytitov.movieexplorer.support

import com.georgiytitov.movieexplorer.domain.model.Movie

object UiTestMovies {
    val first = Movie(
        id = 1L,
        title = "Test movie",
        posterPath = null,
        overview = "A description available without network access",
        releaseDate = "2026-10-15",
        rating = 8.5,
    )
}
