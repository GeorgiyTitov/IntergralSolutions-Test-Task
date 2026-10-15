package com.georgiytitov.movieexplorer.data.remote.network

import com.georgiytitov.movieexplorer.domain.model.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.blackholeSink
import java.io.IOException

internal class PosterCache(private val client: OkHttpClient, private val scope: CoroutineScope) {
    fun store(movies: List<Movie>) {
        scope.launch {
            movies.mapNotNull(Movie::posterUrl).distinct().forEach { url ->
                ensureActive()
                store(url = url)
            }
        }
    }

    private fun store(url: String) {
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body.source().readAll(blackholeSink())
                }
            }
        } catch (_: IOException) {
            // A failed image must not discard the saved movie or block the list.
        }
    }
}
