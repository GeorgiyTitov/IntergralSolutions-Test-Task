package com.georgiytitov.movieexplorer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MovieDao {
    @Query("SELECT * FROM movie_pages WHERE category = :category AND page = :page")
    abstract suspend fun page(
        category: String,
        page: Int,
    ): MoviePageEntity?

    @Query("SELECT * FROM movies WHERE id IN (:ids)")
    abstract suspend fun movies(ids: List<Long>): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE id = :id")
    abstract suspend fun movie(id: Long): MovieEntity?

    @Upsert abstract suspend fun putMovies(movies: List<MovieEntity>)

    @Upsert abstract suspend fun putMovie(movie: MovieEntity)

    @Upsert abstract suspend fun putPage(page: MoviePageEntity)

    @Upsert abstract suspend fun putFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM movie_pages WHERE category = :category")
    abstract suspend fun clearPages(category: String)

    @Transaction
    open suspend fun cache(
        page: MoviePageEntity,
        movies: List<MovieEntity>,
    ) {
        if (page.page == 1) clearPages(page.category)
        putMovies(movies)
        putPage(page)
    }

    @Query(
        "SELECT m.* FROM movies m INNER JOIN favorites f ON f.movieId = m.id " +
            "WHERE f.desired = 1 ORDER BY f.modifiedAt DESC",
    )
    abstract fun observeFavorites(): Flow<List<MovieEntity>>

    @Transaction
    open suspend fun setFavorite(
        movie: MovieEntity,
        favorite: Boolean,
    ) {
        putMovie(movie)
        putFavorite(FavoriteEntity(movie.id, favorite, modifiedAt = System.currentTimeMillis()))
    }

    @Query("SELECT * FROM favorites WHERE pending = 1 ORDER BY modifiedAt")
    abstract suspend fun pendingFavorites(): List<FavoriteEntity>

    @Query(
        "UPDATE favorites SET pending = 0 " +
            "WHERE movieId = :id AND modifiedAt = :version AND desired = :desired",
    )
    abstract suspend fun acknowledgeFavorite(
        id: Long,
        version: Long,
        desired: Boolean,
    )
}
