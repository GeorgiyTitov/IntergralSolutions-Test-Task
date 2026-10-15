package com.georgiytitov.movieexplorer.data.remote.tmdb

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class TmdbAuthenticationInterceptor(
    private val token: String,
    private val apiKey: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (token.isBlank() &&
            apiKey.isBlank()
        ) {
            throw IOException("Configure TMDB credentials in .env and rebuild the app.")
        }
        val request =
            chain
                .request()
                .newBuilder()
                .apply {
                    if (token.isNotBlank()) header("Authorization", "Bearer $token") else {
                        url(
                            chain
                                .request()
                                .url
                                .newBuilder()
                                .addQueryParameter("api_key", apiKey)
                                .build(),
                        )
                    }
                }.build()
        return chain.proceed(request)
    }
}
