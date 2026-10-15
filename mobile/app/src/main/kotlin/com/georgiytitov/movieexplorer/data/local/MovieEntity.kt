package com.georgiytitov.movieexplorer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val posterPath: String?,
    val overview: String,
    val releaseDate: String,
    val rating: Double,
)
