package com.georgiytitov.movieexplorer.data.remote.tmdb

import com.georgiytitov.movieexplorer.data.remote.tmdb.dto.TmdbMovieDto
import com.georgiytitov.movieexplorer.data.remote.tmdb.dto.TmdbPageDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/{tab}")
    suspend fun movies(
        @Path("tab") tab: String,
        @Query("page") page: Int,
        @Query("language") language: String = DEFAULT_LANGUAGE,
    ): TmdbPageDto

    @GET("movie/{id}")
    suspend fun movie(
        @Path("id") id: Long,
        @Query("language") language: String = DEFAULT_LANGUAGE,
    ): TmdbMovieDto

    private companion object {
        const val DEFAULT_LANGUAGE = "en-US"
    }
}
