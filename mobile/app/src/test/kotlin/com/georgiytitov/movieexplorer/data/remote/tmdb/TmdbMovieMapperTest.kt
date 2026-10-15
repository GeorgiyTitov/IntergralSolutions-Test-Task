package com.georgiytitov.movieexplorer.data.remote.tmdb

import com.georgiytitov.movieexplorer.data.remote.tmdb.dto.TmdbMovieDto
import com.georgiytitov.movieexplorer.support.TestMovies
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TmdbMovieMapperTest {
    @Test
    fun missingOptionalDataProducesUsableDefaults() {
        val movie = TmdbMovieDto(TestMovies.first.id, null, null, null, null, null).toDomain()
        assertEquals("", movie.title)
        assertEquals("", movie.overview)
        assertEquals("", movie.year)
        assertEquals(0.0, movie.rating, 0.0)
        assertNull(movie.posterUrl)
    }

    @Test
    fun blankPosterPathDoesNotProduceAnInvalidImageUrl() {
        val movie = TmdbMovieDto(TestMovies.first.id, null, " ", null, null, null).toDomain()
        assertNull(movie.posterPath)
        assertNull(movie.posterUrl)
    }

    @Test
    fun completeMoviePreservesFieldsAndBuildsYearAndPosterUrl() {
        val expected = TestMovies.first
        val movie = TmdbMovieDto(
            expected.id,
            expected.title,
            expected.posterPath,
            expected.overview,
            expected.releaseDate,
            expected.rating,
        ).toDomain()
        assertEquals(expected, movie)
        assertEquals("2026", movie.year)
        assertEquals("https://image.tmdb.org/t/p/w500/first.jpg", movie.posterUrl)
    }
}
