package com.georgiytitov.movieexplorer.data.local

import androidx.room.Entity

@Entity(tableName = "movie_pages", primaryKeys = ["category", "page"])
data class MoviePageEntity(
    val category: String,
    val page: Int,
    val totalPages: Int,
    val movieIds: String,
)
