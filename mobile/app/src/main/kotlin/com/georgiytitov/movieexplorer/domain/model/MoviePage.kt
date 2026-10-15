package com.georgiytitov.movieexplorer.domain.model

data class MoviePage(
    val page: Int,
    val totalPages: Int,
    val movies: List<Movie>,
    val origin: DataOrigin,
) {
    companion object {
        const val FIRST_PAGE = 1
        const val NEXT_PAGE_STEP = 1
    }
}
