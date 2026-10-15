package com.georgiytitov.movieexplorer.data

import com.georgiytitov.movieexplorer.data.local.MovieDao
import com.georgiytitov.movieexplorer.data.local.MoviePageEntity
import com.georgiytitov.movieexplorer.data.local.toEntity
import com.georgiytitov.movieexplorer.data.remote.network.NetworkMonitor
import com.georgiytitov.movieexplorer.data.remote.network.PosterCache
import com.georgiytitov.movieexplorer.domain.model.DataOrigin.CACHE
import com.georgiytitov.movieexplorer.domain.model.MovieTab
import com.georgiytitov.movieexplorer.domain.model.MovieTab.NOW_PLAYING
import com.georgiytitov.movieexplorer.support.FakeMovieDataSource
import com.georgiytitov.movieexplorer.support.TestMovies
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import java.io.IOException

class DefaultMovieRepositoryTest {
    private val dao = mock(MovieDao::class.java)
    private val network = mock(NetworkMonitor::class.java)
    private val posters = mock(PosterCache::class.java)
    private val online = MutableStateFlow(true)
    private val remote = FakeMovieDataSource()
    private lateinit var repository: DefaultMovieRepository

    @Before
    fun setUp() {
        `when`(network.online).thenReturn(online)
        repository = DefaultMovieRepository(remote, dao, network, posters)
    }

    @Test
    fun offlinePageKeepsTheSavedOrderAndSkipsNetwork() {
        runTest {
            online.value = false
            `when`(dao.page(NOW_PLAYING.apiValue, 1))
                .thenReturn(MoviePageEntity(NOW_PLAYING.apiValue, 1, 2, "2,1"))
            `when`(dao.movies(listOf(2L, 1L)))
                .thenReturn(listOf(TestMovies.first.toEntity(), TestMovies.second.toEntity()))
            val result = repository.movies(NOW_PLAYING, 1)
            assertEquals(listOf(TestMovies.second, TestMovies.first), result.movies)
            assertEquals(CACHE, result.origin)
            assertEquals(CACHE, repository.origin.value)
            assertTrue(remote.pageRequests.isEmpty())
        }
    }

    @Test
    fun exhaustedSourcesUseSavedDetails() {
        runTest {
            remote.loadMovie = { throw IOException("Both sources unavailable") }
            `when`(dao.movie(TestMovies.first.id)).thenReturn(TestMovies.first.toEntity())
            assertEquals(TestMovies.first to CACHE, repository.movie(TestMovies.first.id))
            assertEquals(CACHE, repository.origin.value)
        }
    }

    @Test
    fun missingOfflineDataReportsFailureInsteadOfAnEmptySuccess() {
        runTest {
            online.value = false
            try {
                repository.movies(NOW_PLAYING, 1)
                fail("An uncached page must report an error")
            } catch (_: IOException) {
                assertTrue(remote.pageRequests.isEmpty())
            }
        }
    }

    @Test
    fun loadedPageSavesItsMappingAndPreparesPosters() {
        runTest {
            repository.movies(NOW_PLAYING, 1)
            verify(dao).cache(
                MoviePageEntity(NOW_PLAYING.apiValue, 1, 2, "1"),
                listOf(TestMovies.first.toEntity()),
            )
            verify(posters).store(listOf(TestMovies.first))
        }
    }

    @Test
    fun offlinePreparationCoversThreePagesOfEveryTab() {
        runTest {
            remote.loadPage = { _, page -> TestMovies.page(number = page, total = 10) }
            repository.prepareOfflineCache()
            val expected = MovieTab.entries.flatMap { tab -> (1..3).map { page -> tab to page } }
            assertEquals(expected.toSet(), remote.pageRequests.toSet())
            assertEquals(expected.size, remote.pageRequests.size)
        }
    }

    @Test
    fun cancellationDoesNotReadCacheOrStartPosterDownloads() {
        runTest {
            val cancellation = CancellationException("Screen closed")
            remote.loadMovie = { throw cancellation }
            try {
                repository.movie(TestMovies.first.id)
                fail("Cancellation must propagate")
            } catch (actual: CancellationException) {
                assertSame(cancellation, actual)
            }
            verifyNoInteractions(dao, posters)
        }
    }
}
