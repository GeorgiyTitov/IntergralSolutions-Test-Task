package com.georgiytitov.movies.tmdb;

final class TmdbConstants {
    static final String BASE_URL = "https://api.themoviedb.org/3";
    static final String MOVIE_PATH = "/movie/";
    static final String LANGUAGE_PARAMETER = "language";
    static final String DEFAULT_LANGUAGE = "en-US";
    static final String PAGE_PARAMETER = "page";
    static final String API_KEY_PARAMETER = "api_key";
    static final String TOKEN_PROPERTY = "${tmdb.token}";
    static final String API_KEY_PROPERTY = "${tmdb.api-key}";
    static final String EMPTY_TEXT = "";
    static final int CONNECT_TIMEOUT_SECONDS = 3;
    static final int READ_TIMEOUT_SECONDS = 8;

    private TmdbConstants() {
    }
}
