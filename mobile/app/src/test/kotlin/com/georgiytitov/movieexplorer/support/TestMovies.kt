package com.georgiytitov.movieexplorer.support

import com.georgiytitov.movieexplorer.domain.model.DataOrigin
import com.georgiytitov.movieexplorer.domain.model.DataOrigin.BACKEND
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MoviePage

object TestMovies {
    val first = Movie(
        id = 1L,
        title = "First movie",
        posterPath = "/first.jpg",
        overview = "A movie for testing",
        releaseDate = "2026-10-15",
        rating = 8.5,
    )
    val second = first.copy(id = 2L, title = "Second movie")

    fun page(
        number: Int = 1,
        total: Int = 2,
        movies: List<Movie> = listOf(first),
        origin: DataOrigin = BACKEND,
    ): MoviePage {
        return MoviePage(page = number, totalPages = total, movies = movies, origin = origin)
    }
}
