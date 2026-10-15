package com.georgiytitov.movieexplorer.data.remote.tmdb.dto

import com.google.gson.annotations.SerializedName

internal data class TmdbMovieDto(
    val id: Long,
    val title: String?,
    @SerializedName("poster_path") val posterPath: String?,
    val overview: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
)
