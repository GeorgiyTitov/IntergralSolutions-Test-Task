package com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi

import androidx.lifecycle.viewModelScope
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesSyncStatus.FAILED
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesSyncStatus.IDLE
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesSyncStatus.SYNCED
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesSyncStatus.SYNCING
import com.georgiytitov.movieexplorer.support.FakeFavoritesRepository
import com.georgiytitov.movieexplorer.support.MainDispatcherRule
import com.georgiytitov.movieexplorer.support.TestMovies
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {
    @get:Rule val mainDispatcher = MainDispatcherRule()
    private val repository = FakeFavoritesRepository()
    private lateinit var viewModel: FavoritesViewModel

    @After
    fun tearDown() {
        if (::viewModel.isInitialized) {
            viewModel.viewModelScope.cancel()
        }
    }

    @Test
    fun immediateFailuresStillShowProgressOnEveryRetry() {
        runTest {
            repository.syncFailed.value = true
            repository.synchronizeAction = { repository.syncFailed.value = true }
            viewModel = FavoritesViewModel(repository)
            runCurrent()
            assertEquals(FAILED, viewModel.state.value.syncStatus)

            repeat(2) {
                viewModel.handleEvent(FavoritesEvent.DismissSyncStatus)
                viewModel.handleEvent(FavoritesEvent.RetrySync)
                runCurrent()
                assertEquals(SYNCING, viewModel.state.value.syncStatus)
                advanceUntilIdle()
                assertEquals(FAILED, viewModel.state.value.syncStatus)
            }
            assertEquals(2, repository.synchronizationCount)
        }
    }

    @Test
    fun repeatedRetryWhileRequestIsRunningDoesNotStartAnotherRequest() {
        runTest {
            val response = CompletableDeferred<Unit>()
            repository.synchronizeAction = { response.await() }
            viewModel = FavoritesViewModel(repository)
            runCurrent()
            viewModel.handleEvent(FavoritesEvent.RetrySync)
            viewModel.handleEvent(FavoritesEvent.RetrySync)
            runCurrent()
            assertEquals(1, repository.synchronizationCount)
            advanceUntilIdle()
            assertEquals(SYNCING, viewModel.state.value.syncStatus)
            response.complete(Unit)
            runCurrent()
            assertEquals(SYNCED, viewModel.state.value.syncStatus)
        }
    }

    @Test
    fun successfulRetryKeepsItsResultUntilDismissed() {
        runTest {
            repository.syncFailed.value = true
            viewModel = FavoritesViewModel(repository)
            runCurrent()
            viewModel.handleEvent(FavoritesEvent.RetrySync)
            advanceUntilIdle()
            assertEquals(SYNCED, viewModel.state.value.syncStatus)
            viewModel.handleEvent(FavoritesEvent.DismissSyncStatus)
            assertEquals(IDLE, viewModel.state.value.syncStatus)
        }
    }

    @Test
    fun localFavoritesRemainVisibleAfterSynchronizationFails() {
        runTest {
            repository.favorites.value = listOf(TestMovies.first)
            viewModel = FavoritesViewModel(repository)
            runCurrent()
            repository.syncFailed.value = true
            runCurrent()
            assertEquals(listOf(TestMovies.first), viewModel.state.value.movies)
            assertEquals(FAILED, viewModel.state.value.syncStatus)
            assertFalse(viewModel.state.value.loading)
        }
    }

    @Test
    fun failedLocalChangeReportsAnErrorAndKeepsTheFavorite() {
        runTest {
            repository.favorites.value = listOf(TestMovies.first)
            repository.toggleAction = { throw IOException("Storage unavailable") }
            viewModel = FavoritesViewModel(repository)
            runCurrent()
            viewModel.handleEvent(FavoritesEvent.Toggle(TestMovies.first))
            runCurrent()
            assertEquals(R.string.save_favorite_error, viewModel.state.value.error)
            assertTrue(viewModel.state.value.movies.contains(TestMovies.first))
            viewModel.handleEvent(FavoritesEvent.DismissError)
            assertEquals(null, viewModel.state.value.error)
        }
    }
}
