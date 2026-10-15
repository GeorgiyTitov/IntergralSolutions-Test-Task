package com.georgiytitov.movieexplorer.presentation.navigation

internal enum class MovieRoute(val path: String) {
    MOVIES("browse"),
    FAVORITES("favorites"),
    DETAILS("movie/{id}"),
    ;

    fun withMovieId(movieId: Long) = path.replace("{$MOVIE_ID}", movieId.toString())

    companion object {
        const val MOVIE_ID = "id"
    }
}
