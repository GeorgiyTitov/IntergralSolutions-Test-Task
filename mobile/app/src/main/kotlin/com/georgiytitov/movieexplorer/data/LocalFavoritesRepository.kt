package com.georgiytitov.movieexplorer.data

import com.georgiytitov.movieexplorer.data.local.MovieDao
import com.georgiytitov.movieexplorer.data.local.toDomain
import com.georgiytitov.movieexplorer.data.local.toEntity
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.map

class LocalFavoritesRepository(
    private val dao: MovieDao,
) : FavoritesRepository {
    override val favorites =
        dao
            .observeFavorites()
            .map { movies -> movies.map { it.toDomain() } }

    override suspend fun setFavorite(
        movie: Movie,
        favorite: Boolean,
    ) = dao.setFavorite(movie.toEntity(), favorite)
}
