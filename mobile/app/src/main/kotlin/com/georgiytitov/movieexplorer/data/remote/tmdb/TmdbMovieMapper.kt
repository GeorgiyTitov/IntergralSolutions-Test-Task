package com.georgiytitov.movieexplorer.data.remote.tmdb

import com.georgiytitov.movieexplorer.data.remote.tmdb.dto.TmdbMovieDto
import com.georgiytitov.movieexplorer.domain.model.Movie

internal fun TmdbMovieDto.toDomain() = Movie(
    id = id,
    title = title.orEmpty().ifBlank { "Untitled" },
    posterPath = posterPath?.takeIf(String::isNotBlank),
    overview = overview.orEmpty(),
    releaseDate = releaseDate.orEmpty(),
    rating = voteAverage ?: 0.0,
)
