package com.georgiytitov.movieexplorer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MovieEntity::class, MoviePageEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movies(): MovieDao
}
