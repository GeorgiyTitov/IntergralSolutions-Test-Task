package com.georgiytitov.movieexplorer.data.remote.tmdb

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class TmdbAuthenticationInterceptor(
    private val token: String,
    private val apiKey: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (token.isBlank() && apiKey.isBlank()) {
            throw IOException(MISSING_CREDENTIALS_ERROR)
        }
        val request = chain
            .request()
            .newBuilder()
            .apply {
                if (token.isNotBlank()) {
                    header(AUTHORIZATION_HEADER, "$BEARER_SCHEME $token")
                } else {
                    url(
                        chain
                            .request()
                            .url
                            .newBuilder()
                            .addQueryParameter(API_KEY_PARAMETER, apiKey)
                            .build(),
                    )
                }
            }.build()
        return chain.proceed(request)
    }

    private companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_SCHEME = "Bearer"
        const val API_KEY_PARAMETER = "api_key"
        const val MISSING_CREDENTIALS_ERROR =
            "Configure TMDB credentials in .env and rebuild the app."
    }
}
