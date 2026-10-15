package com.georgiytitov.movieexplorer.di

import android.content.Context
import androidx.room.Room
import com.georgiytitov.movieexplorer.BuildConfig
import com.georgiytitov.movieexplorer.data.DefaultFavoritesRepository
import com.georgiytitov.movieexplorer.data.DefaultMovieRepository
import com.georgiytitov.movieexplorer.data.local.InstallationId
import com.georgiytitov.movieexplorer.data.local.MovieDatabase
import com.georgiytitov.movieexplorer.data.remote.FallbackMovieDataSource
import com.georgiytitov.movieexplorer.data.remote.NetworkMonitor
import com.georgiytitov.movieexplorer.data.remote.backend.BackendApi
import com.georgiytitov.movieexplorer.data.remote.backend.BackendDataSource
import com.georgiytitov.movieexplorer.data.remote.tmdb.TmdbApi
import com.georgiytitov.movieexplorer.data.remote.tmdb.TmdbAuthenticationInterceptor
import com.georgiytitov.movieexplorer.data.remote.tmdb.TmdbDataSource
import com.georgiytitov.movieexplorer.domain.model.DataOrigin
import com.georgiytitov.movieexplorer.domain.repository.FavoritesRepository
import com.georgiytitov.movieexplorer.domain.repository.MovieRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppGraph(
    context: Context,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val network = NetworkMonitor(context)
    private val database = Room
        .databaseBuilder(
            context,
            MovieDatabase::class.java,
            "movies.db",
        ).build()
    private val client = OkHttpClient
        .Builder()
        .connectTimeout(4, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .callTimeout(12, TimeUnit.SECONDS)
        .build()
    private val tmdbApi = Retrofit
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
    private val backendApi = Retrofit
        .Builder()
        .baseUrl(BuildConfig.BACKEND_URL.trimEnd('/') + "/")
        .client(client.newBuilder().callTimeout(4, TimeUnit.SECONDS).build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BackendApi::class.java)
    val movies: MovieRepository = DefaultMovieRepository(
        remote = FallbackMovieDataSource(
            primary = BackendDataSource(backendApi),
            fallback = TmdbDataSource(tmdbApi),
        ),
        dao = database.movies(),
        network = network,
    )
    val favorites: FavoritesRepository = DefaultFavoritesRepository(
        dao = database.movies(),
        backend = backendApi,
        userId = InstallationId(context).value,
        network = network,
        scope = scope,
    )

    init {
        scope.launch {
            combine(network.online, movies.origin) { online, origin ->
                online && origin == DataOrigin.BACKEND
            }.distinctUntilChanged().filter { it }.collect {
                favorites.synchronize()
            }
        }
    }
}
