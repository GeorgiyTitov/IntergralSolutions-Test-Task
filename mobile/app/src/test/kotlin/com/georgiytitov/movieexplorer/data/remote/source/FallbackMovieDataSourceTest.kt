package com.georgiytitov.movieexplorer.data.remote.source

import com.georgiytitov.movieexplorer.domain.model.DataOrigin.TMDB
import com.georgiytitov.movieexplorer.domain.model.MovieTab.NOW_PLAYING
import com.georgiytitov.movieexplorer.support.FakeMovieDataSource
import com.georgiytitov.movieexplorer.support.TestMovies
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.IOException

class FallbackMovieDataSourceTest {
    private val primary = FakeMovieDataSource()
    private val fallback = FakeMovieDataSource()
    private val source = FallbackMovieDataSource(primary, fallback)

    @Test
    fun healthyBackendDoesNotCallTmdb() {
        runTest {
            assertEquals(TestMovies.page(), source.movies(NOW_PLAYING, 1))
            assertTrue(fallback.pageRequests.isEmpty())
        }
    }

    @Test
    fun backendFailureLoadsTheSamePageFromTmdb() {
        runTest {
            primary.loadPage = { _, _ -> throw IOException("Backend unavailable") }
            fallback.loadPage = { _, page -> TestMovies.page(number = page, origin = TMDB) }
            val result = source.movies(NOW_PLAYING, 2)
            assertEquals(TMDB, result.origin)
            assertEquals(listOf(NOW_PLAYING to 2), fallback.pageRequests)
        }
    }

    @Test
    fun backendFailureLoadsTheSameMovieFromTmdb() {
        runTest {
            primary.loadMovie = { throw IOException("Backend unavailable") }
            fallback.loadMovie = { TestMovies.first to TMDB }
            assertEquals(TestMovies.first to TMDB, source.movie(TestMovies.first.id))
            assertEquals(listOf(TestMovies.first.id), fallback.detailRequests)
        }
    }

    @Test
    fun cancellationNeverTriggersAnotherNetworkRequest() {
        runTest {
            val cancellation = CancellationException("Tab changed")
            primary.loadPage = { _, _ -> throw cancellation }
            try {
                source.movies(NOW_PLAYING, 1)
                fail("Cancellation must propagate")
            } catch (actual: CancellationException) {
                assertSame(cancellation, actual)
            }
            assertTrue(fallback.pageRequests.isEmpty())
        }
    }

    @Test
    fun errorFromBothSourcesPropagatesToTheCacheLayer() {
        runTest {
            val error = IOException("TMDB unavailable")
            primary.loadMovie = { throw IOException("Backend unavailable") }
            fallback.loadMovie = { throw error }
            try {
                source.movie(TestMovies.first.id)
                fail("The repository must receive the failure")
            } catch (actual: IOException) {
                assertSame(error, actual)
            }
        }
    }
}
