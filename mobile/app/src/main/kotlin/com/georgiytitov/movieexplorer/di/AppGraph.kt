package com.georgiytitov.movieexplorer.di

import android.content.Context
import androidx.room.Room
import com.georgiytitov.movieexplorer.BuildConfig
import com.georgiytitov.movieexplorer.data.DefaultMovieRepository
import com.georgiytitov.movieexplorer.data.LocalFavoritesRepository
import com.georgiytitov.movieexplorer.data.local.MovieDatabase
import com.georgiytitov.movieexplorer.data.remote.NetworkMonitor
import com.georgiytitov.movieexplorer.data.remote.tmdb.TmdbApi
import com.georgiytitov.movieexplorer.data.remote.tmdb.TmdbAuthenticationInterceptor
import com.georgiytitov.movieexplorer.data.remote.tmdb.TmdbDataSource
import com.georgiytitov.movieexplorer.domain.repository.MovieRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppGraph(
    context: Context,
) {
    val network = NetworkMonitor(context)
    private val database =
        Room
            .databaseBuilder(
                context,
                MovieDatabase::class.java,
                "movies.db",
            ).build()
    private val client =
        OkHttpClient
            .Builder()
            .connectTimeout(4, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .callTimeout(12, TimeUnit.SECONDS)
            .build()
    private val tmdbApi =
        Retrofit
            .Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(
                client
                    .newBuilder()
                    .addInterceptor(
                        TmdbAuthenticationInterceptor(
                            BuildConfig.TMDB_READ_ACCESS_TOKEN,
                            BuildConfig.TMDB_API_KEY,
                        ),
                    ).build(),
            ).addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)
    val movies: MovieRepository =
        DefaultMovieRepository(
            TmdbDataSource(tmdbApi),
            database.movies(),
            network,
        )
    val favorites = LocalFavoritesRepository(database.movies())
}
