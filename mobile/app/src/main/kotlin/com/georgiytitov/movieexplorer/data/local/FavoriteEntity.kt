package com.georgiytitov.movieexplorer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
internal data class FavoriteEntity(
    @PrimaryKey val movieId: Long,
    val savedAt: Long,
)
