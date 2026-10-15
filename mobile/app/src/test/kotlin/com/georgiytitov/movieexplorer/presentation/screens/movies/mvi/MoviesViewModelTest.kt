package com.georgiytitov.movieexplorer.presentation.screens.movies.mvi

import androidx.lifecycle.viewModelScope
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.domain.model.DataOrigin.CACHE
import com.georgiytitov.movieexplorer.domain.model.MoviePage
import com.georgiytitov.movieexplorer.domain.model.MovieTab.NOW_PLAYING
import com.georgiytitov.movieexplorer.domain.model.MovieTab.UPCOMING
import com.georgiytitov.movieexplorer.support.FakeMovieRepository
import com.georgiytitov.movieexplorer.support.MainDispatcherRule
import com.georgiytitov.movieexplorer.support.TestMovies
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModelTest {
    @get:Rule val mainDispatcher = MainDispatcherRule()
    private val repository = FakeMovieRepository()
    private lateinit var viewModel: MoviesViewModel

    @After
    fun tearDown() {
        if (::viewModel.isInitialized) {
            viewModel.viewModelScope.cancel()
        }
    }

    @Test
    fun cachedMoviesStayVisibleWhileFreshPageLoads() {
        runTest {
            val response = CompletableDeferred<MoviePage>()
            repository.cachedPage = TestMovies.page(origin = CACHE)
            repository.loadPage = { _, _ -> response.await() }
            viewModel = MoviesViewModel(repository)
            runCurrent()
            assertEquals(listOf(TestMovies.first), viewModel.state.value.movies)
            assertTrue(viewModel.state.value.loading)

            response.complete(TestMovies.page(movies = listOf(TestMovies.second)))
            runCurrent()
            assertEquals(listOf(TestMovies.second), viewModel.state.value.movies)
            assertFalse(viewModel.state.value.loading)
        }
    }

    @Test
    fun paginationIgnoresConcurrentRequestsAndRemovesDuplicates() {
        runTest {
            val nextPage = CompletableDeferred<MoviePage>()
            repository.loadPage = { _, page ->
                if (page == 1) {
                    TestMovies.page()
                } else {
                    nextPage.await()
                }
            }
            viewModel = MoviesViewModel(repository)
            runCurrent()
            viewModel.handleEvent(MoviesEvent.LoadNext)
            viewModel.handleEvent(MoviesEvent.LoadNext)
            runCurrent()
            assertEquals(listOf(NOW_PLAYING to 1, NOW_PLAYING to 2), repository.pageRequests)
            assertTrue(viewModel.state.value.loadingMore)

            nextPage.complete(
                TestMovies.page(number = 2, movies = listOf(TestMovies.first, TestMovies.second)),
            )
            runCurrent()
            viewModel.handleEvent(MoviesEvent.LoadNext)
            runCurrent()
            assertEquals(listOf(TestMovies.first, TestMovies.second), viewModel.state.value.movies)
            assertEquals(2, repository.pageRequests.size)
            assertFalse(viewModel.state.value.loadingMore)
        }
    }

    @Test
    fun switchingTabsCancelsOldRequestWithoutShowingAnError() {
        runTest {
            var oldRequestCancelled = false
            repository.loadPage = { tab, _ ->
                if (tab == NOW_PLAYING) {
                    try {
                        awaitCancellation()
                    } finally {
                        oldRequestCancelled = true
                    }
                } else {
                    TestMovies.page(movies = listOf(TestMovies.second))
                }
            }
            viewModel = MoviesViewModel(repository)
            runCurrent()
            viewModel.handleEvent(MoviesEvent.SelectTab(UPCOMING))
            runCurrent()
            assertTrue(oldRequestCancelled)
            assertEquals(UPCOMING, viewModel.state.value.tab)
            assertEquals(listOf(TestMovies.second), viewModel.state.value.movies)
            assertNull(viewModel.state.value.error)
        }
    }

    @Test
    fun failedNextPageKeepsLoadedMoviesAndRefreshRecovers() {
        runTest {
            repository.loadPage = { _, page ->
                if (page == 2) {
                    throw IOException("Connection lost")
                }
                TestMovies.page()
            }
            viewModel = MoviesViewModel(repository)
            runCurrent()
            viewModel.handleEvent(MoviesEvent.LoadNext)
            runCurrent()
            assertEquals(listOf(TestMovies.first), viewModel.state.value.movies)
            assertEquals(R.string.load_movies_error, viewModel.state.value.error)
            assertFalse(viewModel.state.value.loadingMore)

            viewModel.handleEvent(MoviesEvent.Refresh)
            runCurrent()
            assertNull(viewModel.state.value.error)
            assertEquals(R.string.movies_updated, viewModel.feedbackEvents.first())
        }
    }

    @Test
    fun offlineRefreshReportsThatSavedMoviesAreShown() {
        runTest {
            repository.loadPage = { _, _ -> TestMovies.page(origin = CACHE) }
            viewModel = MoviesViewModel(repository)
            runCurrent()
            viewModel.handleEvent(MoviesEvent.Refresh)
            runCurrent()
            assertEquals(R.string.refresh_cached_movies, viewModel.feedbackEvents.first())
        }
    }

    @Test
    fun emptyResponseIsACompletedEmptyState() {
        runTest {
            repository.loadPage = { _, _ -> TestMovies.page(total = 1, movies = emptyList()) }
            viewModel = MoviesViewModel(repository)
            runCurrent()
            assertTrue(viewModel.state.value.movies.isEmpty())
            assertFalse(viewModel.state.value.loading)
            assertNull(viewModel.state.value.error)
        }
    }
}
