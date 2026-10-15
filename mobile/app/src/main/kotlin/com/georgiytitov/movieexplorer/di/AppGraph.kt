package com.georgiytitov.movieexplorer.di

import android.content.Context
import androidx.room.Room
import coil3.ImageLoader
import com.georgiytitov.movieexplorer.BuildConfig
import com.georgiytitov.movieexplorer.data.DefaultFavoritesRepository
import com.georgiytitov.movieexplorer.data.DefaultMovieRepository
import com.georgiytitov.movieexplorer.data.local.InstallationId
import com.georgiytitov.movieexplorer.data.local.MovieDatabase
import com.georgiytitov.movieexplorer.data.remote.backend.BackendApi
import com.georgiytitov.movieexplorer.data.remote.backend.BackendDataSource
import com.georgiytitov.movieexplorer.data.remote.network.NetworkMonitor
import com.georgiytitov.movieexplorer.data.remote.network.PosterCache
import com.georgiytitov.movieexplorer.data.remote.network.posterHttpClient
import com.georgiytitov.movieexplorer.data.remote.network.posterImageLoader
import com.georgiytitov.movieexplorer.data.remote.source.FallbackMovieDataSource
import com.georgiytitov.movieexplorer.data.remote.tmdb.TmdbApi
import com.georgiytitov.movieexplorer.data.remote.tmdb.TmdbAuthenticationInterceptor
import com.georgiytitov.movieexplorer.data.remote.tmdb.TmdbDataSource
import com.georgiytitov.movieexplorer.domain.repository.FavoritesRepository
import com.georgiytitov.movieexplorer.domain.repository.MovieRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal class AppGraph(
    context: Context,
) {
    private val scope: CoroutineScope
    val network: NetworkMonitor
    private val posterClient: OkHttpClient
    private val posterCache: PosterCache
    val imageLoader: ImageLoader
    private val database: MovieDatabase
    private val client: OkHttpClient
    private val tmdbApi: TmdbApi
    private val backendApi: BackendApi
    val movies: MovieRepository
    val favorites: FavoritesRepository

    init {
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        network = NetworkMonitor(context)
        posterClient = posterHttpClient(context = context, network = network)
        posterCache = PosterCache(client = posterClient, scope = scope)
        imageLoader = posterImageLoader(context = context, client = posterClient)
        database = createDatabase(context = context)
        client = createHttpClient()
        tmdbApi = createTmdbApi()
        backendApi = createBackendApi()
        val movieRepository = createMovieRepository()
        movies = movieRepository
        favorites = createFavoritesRepository(context = context)
        observeNetworkConnection(movieRepository = movieRepository)
    }

    private fun createDatabase(context: Context): MovieDatabase {
        return Room
            .databaseBuilder(context, MovieDatabase::class.java, DATABASE_NAME)
            .build()
    }

    private fun createHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(TMDB_CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    private fun createTmdbApi(): TmdbApi {
        val tmdbClient = client
            .newBuilder()
            .addInterceptor(
                TmdbAuthenticationInterceptor(
                    BuildConfig.TMDB_READ_ACCESS_TOKEN,
                    BuildConfig.TMDB_API_KEY,
                ),
            ).build()
        return createRetrofit(baseUrl = TMDB_BASE_URL, client = tmdbClient)
            .create(TmdbApi::class.java)
    }

    private fun createBackendApi(): BackendApi {
        val backendClient = client
            .newBuilder()
            .callTimeout(BACKEND_CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
        return createRetrofit(baseUrl = BuildConfig.BACKEND_URL, client = backendClient)
            .create(BackendApi::class.java)
    }

    private fun createRetrofit(
        baseUrl: String,
        client: OkHttpClient,
    ): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createMovieRepository(): DefaultMovieRepository {
        return DefaultMovieRepository(
            remote = FallbackMovieDataSource(
                primary = BackendDataSource(backendApi),
                fallback = TmdbDataSource(tmdbApi),
            ),
            dao = database.movies(),
            network = network,
            posters = posterCache,
        )
    }

    private fun createFavoritesRepository(context: Context): FavoritesRepository {
        return DefaultFavoritesRepository(
            dao = database.movies(),
            backend = backendApi,
            userId = InstallationId(context).value,
            network = network,
            scope = scope,
        )
    }

    private fun observeNetworkConnection(movieRepository: DefaultMovieRepository) {
        scope.launch {
            network.online.collectLatest { online ->
                if (online) {
                    coroutineScope {
                        launch { favorites.synchronize() }
                        movieRepository.prepareOfflineCache()
                    }
                }
            }
        }
    }

    private companion object {
        const val DATABASE_NAME = "movies.db"
        const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
        const val CONNECT_TIMEOUT_SECONDS = 4L
        const val READ_TIMEOUT_SECONDS = 10L
        const val TMDB_CALL_TIMEOUT_SECONDS = 12L
        const val BACKEND_CALL_TIMEOUT_SECONDS = 4L
    }
}
