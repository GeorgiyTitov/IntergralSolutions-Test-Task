package com.georgiytitov.movieexplorer.data.local

import com.georgiytitov.movieexplorer.domain.model.Movie

internal fun MovieEntity.toDomain() = Movie(id, title, posterPath, overview, releaseDate, rating)

internal fun Movie.toEntity() = MovieEntity(id, title, posterPath, overview, releaseDate, rating)
