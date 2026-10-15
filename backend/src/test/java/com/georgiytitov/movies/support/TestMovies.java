package com.georgiytitov.movies.support;

import com.georgiytitov.movies.movie.MovieDto;

public final class TestMovies {
    public static final String EMPTY_TEXT = "";
    public static final MovieDto FIRST = new MovieDto(
        1L,
        "First movie",
        "/first.jpg",
        "Test overview",
        "2026-10-15",
        8.5
    );
    public static final MovieDto SECOND = new MovieDto(
        2L,
        "Second movie",
        null,
        "Another movie",
        "2025-01-01",
        7.0
    );

    private TestMovies() {
    }
}
