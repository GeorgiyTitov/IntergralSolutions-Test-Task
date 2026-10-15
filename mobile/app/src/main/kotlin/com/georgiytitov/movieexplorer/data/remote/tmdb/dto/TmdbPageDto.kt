package com.georgiytitov.movieexplorer.data.remote.tmdb.dto

import com.google.gson.annotations.SerializedName

internal data class TmdbPageDto(
    val page: Int,
    @SerializedName("total_pages") val totalPages: Int,
    val results: List<TmdbMovieDto>?,
)
