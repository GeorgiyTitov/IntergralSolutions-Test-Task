package com.georgiytitov.movieexplorer.data.remote

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

/** Source bytes expire after 24 hours online; cached posters remain usable offline. */
fun posterImageLoader(
    context: Context,
    network: NetworkMonitor,
): ImageLoader {
    val client =
        OkHttpClient
            .Builder()
            .cache(Cache(File(context.cacheDir, "posters"), 100L * 1024 * 1024))
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request =
                    if (network.online.value) chain.request() else {
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
                    .header("Cache-Control", "public, max-age=86400")
                    .removeHeader("Expires")
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
