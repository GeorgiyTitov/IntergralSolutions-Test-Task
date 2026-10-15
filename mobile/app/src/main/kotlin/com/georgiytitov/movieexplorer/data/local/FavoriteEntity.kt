package com.georgiytitov.movieexplorer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val movieId: Long,
    val desired: Boolean,
    val pending: Boolean = true,
    val modifiedAt: Long,
)
