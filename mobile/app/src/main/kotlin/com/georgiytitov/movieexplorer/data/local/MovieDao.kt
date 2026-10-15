package com.georgiytitov.movieexplorer.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class MovieDao {
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

    @Transaction
    open suspend fun cache(
        page: MoviePageEntity,
        movies: List<MovieEntity>,
    ) {
        putMovies(movies)
        putPage(page)
    }

    @Query(
        "SELECT m.* FROM movies m INNER JOIN favorites f ON f.movieId = m.id " +
            "ORDER BY f.savedAt DESC",
    )
    abstract fun observeFavorites(): Flow<List<MovieEntity>>

    @Transaction
    open suspend fun toggleFavorite(movie: MovieEntity) {
        if (isFavorite(movie.id)) {
            removeFavorite(movie.id)
        } else {
            putMovie(movie)
            putFavorite(FavoriteEntity(movie.id, savedAt = System.currentTimeMillis()))
        }
    }

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE movieId = :id)")
    abstract suspend fun isFavorite(id: Long): Boolean

    @Query("SELECT movieId FROM favorites")
    abstract suspend fun favoriteIds(): List<Long>

    @Query("DELETE FROM favorites WHERE movieId = :id")
    abstract suspend fun removeFavorite(id: Long)
}
