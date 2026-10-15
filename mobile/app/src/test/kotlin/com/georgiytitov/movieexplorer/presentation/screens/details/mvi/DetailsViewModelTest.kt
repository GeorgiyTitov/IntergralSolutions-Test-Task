package com.georgiytitov.movieexplorer.presentation.screens.details.mvi

import androidx.lifecycle.viewModelScope
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.domain.model.DataOrigin
import com.georgiytitov.movieexplorer.domain.model.DataOrigin.BACKEND
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.presentation.navigation.NavigationEvent
import com.georgiytitov.movieexplorer.support.FakeMovieRepository
import com.georgiytitov.movieexplorer.support.MainDispatcherRule
import com.georgiytitov.movieexplorer.support.TestMovies
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class DetailsViewModelTest {
    @get:Rule val mainDispatcher = MainDispatcherRule()
    private val repository = FakeMovieRepository()
    private lateinit var viewModel: DetailsViewModel

    @After
    fun tearDown() {
        if (::viewModel.isInitialized) {
            viewModel.viewModelScope.cancel()
        }
    }

    @Test
    fun cachedDetailsStayVisibleWhileFreshDetailsLoad() {
        runTest {
            val response = CompletableDeferred<Pair<Movie, DataOrigin>>()
            repository.cachedDetails = TestMovies.first
            repository.loadMovie = { response.await() }
            viewModel = DetailsViewModel(TestMovies.first.id, repository)
            runCurrent()
            assertEquals(TestMovies.first, viewModel.state.value.movie)
            assertTrue(viewModel.state.value.loading)

            response.complete(TestMovies.second to BACKEND)
            runCurrent()
            assertEquals(TestMovies.second, viewModel.state.value.movie)
            assertFalse(viewModel.state.value.loading)
        }
    }

    @Test
    fun failureKeepsCachedDetailsAndRetryCanRecover() {
        runTest {
            repository.cachedDetails = TestMovies.first
            repository.loadMovie = { throw IOException("Connection lost") }
            viewModel = DetailsViewModel(TestMovies.first.id, repository)
            runCurrent()
            assertEquals(TestMovies.first, viewModel.state.value.movie)
            assertEquals(R.string.load_details_error, viewModel.state.value.error)
            assertFalse(viewModel.state.value.loading)

            repository.loadMovie = { TestMovies.first to BACKEND }
            viewModel.handleEvent(DetailsEvent.Refresh)
            runCurrent()
            assertNull(viewModel.state.value.error)
            assertEquals(TestMovies.first, viewModel.state.value.movie)
        }
    }

    @Test
    fun backNavigatesWithoutStartingAnotherRequest() {
        runTest {
            viewModel = DetailsViewModel(TestMovies.first.id, repository)
            runCurrent()
            viewModel.handleEvent(DetailsEvent.Back)
            runCurrent()
            assertEquals(NavigationEvent.Back, viewModel.navigationEvents.first())
            assertEquals(listOf(TestMovies.first.id), repository.detailRequests)
        }
    }
}
