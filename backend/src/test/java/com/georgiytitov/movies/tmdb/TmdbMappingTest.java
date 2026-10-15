package com.georgiytitov.movies.tmdb;

import static com.georgiytitov.movies.support.TestMovies.EMPTY_TEXT;
import static com.georgiytitov.movies.support.TestMovies.FIRST;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.georgiytitov.movies.movie.MoviePagination;

import java.util.List;

import org.junit.jupiter.api.Test;

class TmdbMappingTest {
    @Test
    void missingOptionalFieldsBecomeEmptyText() {
        var movie = new TmdbMovie(
            FIRST.id(),
            FIRST.title(),
            null,
            null,
            null,
            FIRST.rating()
        ).toDto();
        assertEquals(EMPTY_TEXT, movie.overview());
        assertEquals(EMPTY_TEXT, movie.releaseDate());
        assertNull(movie.posterPath());
    }

    @Test
    void completeMoviePreservesThePublicContract() {
        var movie = new TmdbMovie(
            FIRST.id(),
            FIRST.title(),
            FIRST.posterPath(),
            FIRST.overview(),
            FIRST.releaseDate(),
            FIRST.rating()
        );
        assertEquals(FIRST, movie.toDto());
    }

    @Test
    void totalPagesAreCappedAtTheLastPageTmdbAllows() {
        var movie = new TmdbMovie(
            FIRST.id(),
            FIRST.title(),
            FIRST.posterPath(),
            FIRST.overview(),
            FIRST.releaseDate(),
            FIRST.rating()
        );
        var page = new TmdbPage(2, 900, List.of(movie)).toDto();
        assertEquals(2, page.page());
        assertEquals(MoviePagination.MAX_PAGE, page.totalPages());
        assertEquals(List.of(FIRST), page.movies());
    }
}
