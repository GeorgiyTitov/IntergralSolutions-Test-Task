package com.georgiytitov.movieexplorer.di

import com.georgiytitov.movieexplorer.BuildConfig
import com.georgiytitov.movieexplorer.data.DefaultMovieRepository
import com.georgiytitov.movieexplorer.data.remote.tmdb.TmdbApi
import com.georgiytitov.movieexplorer.data.remote.tmdb.TmdbAuthenticationInterceptor
import com.georgiytitov.movieexplorer.data.remote.tmdb.TmdbDataSource
import com.georgiytitov.movieexplorer.domain.repository.MovieRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppGraph {
    private val client = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .callTimeout(12, TimeUnit.SECONDS)
        .build()
    private val tmdbApi = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(client.newBuilder().addInterceptor(TmdbAuthenticationInterceptor(BuildConfig.TMDB_READ_ACCESS_TOKEN, BuildConfig.TMDB_API_KEY)).build())
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(TmdbApi::class.java)
    val movies: MovieRepository = DefaultMovieRepository(TmdbDataSource(tmdbApi))
}
