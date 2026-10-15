package com.georgiytitov.movieexplorer.data.remote

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

private const val CACHE_DIRECTORY = "posters"
private const val CACHE_SIZE_BYTES = 100L * 1024 * 1024
private const val CACHE_TTL_SECONDS = 86_400
private const val CONNECT_TIMEOUT_SECONDS = 5L
private const val READ_TIMEOUT_SECONDS = 10L
private const val CACHE_CONTROL_HEADER = "Cache-Control"
private const val EXPIRES_HEADER = "Expires"
private const val CACHE_CONTROL_VALUE = "public, max-age=$CACHE_TTL_SECONDS"

/** Source bytes expire after 24 hours online; cached posters remain usable offline. */
fun posterImageLoader(
    context: Context,
    network: NetworkMonitor,
): ImageLoader {
    val client =
        OkHttpClient
            .Builder()
            .cache(Cache(File(context.cacheDir, CACHE_DIRECTORY), CACHE_SIZE_BYTES))
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request =
                    if (network.online.value) {
                        chain.request()
                    } else {
                        chain
                            .request()
                            .newBuilder()
                            .cacheControl(
                                CacheControl
                                    .Builder()
                                    .onlyIfCached()
                                    .maxStale(
                                        Int.MAX_VALUE,
                                        TimeUnit.SECONDS,
                                    ).build(),
                            ).build()
                    }
                chain.proceed(request)
            }.addNetworkInterceptor { chain ->
                chain
                    .proceed(chain.request())
                    .newBuilder()
                    .header(CACHE_CONTROL_HEADER, CACHE_CONTROL_VALUE)
                    .removeHeader(EXPIRES_HEADER)
                    .build()
            }.build()
    // One HTTP disk cache owns expiry. Disabling Coil's parallel caches avoids bypassing it.
    return ImageLoader
        .Builder(context)
        .diskCache(null)
        .memoryCache(null)
        .components { add(OkHttpNetworkFetcherFactory(callFactory = { client })) }
        .build()
}
