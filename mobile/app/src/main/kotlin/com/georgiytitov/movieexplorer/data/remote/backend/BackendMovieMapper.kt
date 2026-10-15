package com.georgiytitov.movieexplorer.data.remote.backend

import com.georgiytitov.movieexplorer.data.remote.backend.dto.BackendMovieDto
import com.georgiytitov.movieexplorer.domain.model.Movie

internal fun BackendMovieDto.toDomain() =
    Movie(
        id = id,
        title = title.orEmpty(),
        posterPath = posterPath,
        overview = overview.orEmpty(),
        releaseDate = releaseDate.orEmpty(),
        rating = rating,
    )
