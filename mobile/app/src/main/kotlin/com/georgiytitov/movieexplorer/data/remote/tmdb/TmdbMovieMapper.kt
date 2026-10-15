package com.georgiytitov.movieexplorer.data.remote.tmdb

import com.georgiytitov.movieexplorer.data.remote.tmdb.dto.TmdbMovieDto
import com.georgiytitov.movieexplorer.domain.model.Movie

private const val DEFAULT_RATING = 0.0

internal fun TmdbMovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title.orEmpty(),
        posterPath = posterPath?.takeIf(String::isNotBlank),
        overview = overview.orEmpty(),
        releaseDate = releaseDate.orEmpty(),
        rating = voteAverage ?: DEFAULT_RATING,
    )
}
