package com.georgiytitov.movieexplorer.data.remote.backend

import com.georgiytitov.movieexplorer.data.remote.backend.dto.AddFavoriteDto
import com.georgiytitov.movieexplorer.data.remote.backend.dto.BackendMovieDto
import com.georgiytitov.movieexplorer.data.remote.backend.dto.BackendPageDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface BackendApi {
    @GET("movies")
    suspend fun movies(
        @Query("tab") tab: String,
        @Query("page") page: Int,
    ): BackendPageDto

    @GET("movies/{id}")
    suspend fun movie(
        @Path("id") id: Long,
    ): BackendMovieDto

    @GET("users/{userId}/favorites")
    suspend fun favorites(
        @Path("userId") userId: String,
    ): List<BackendMovieDto>

    @POST("users/{userId}/favorites")
    suspend fun addFavorite(
        @Path("userId") userId: String,
        @Body request: AddFavoriteDto,
    ): BackendMovieDto

    @DELETE("users/{userId}/favorites/{movieId}")
    suspend fun removeFavorite(
        @Path("userId") userId: String,
        @Path("movieId") movieId: Long,
    )
}
