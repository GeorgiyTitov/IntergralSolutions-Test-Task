package com.georgiytitov.movieexplorer.data

import com.georgiytitov.movieexplorer.data.local.MovieDao
import com.georgiytitov.movieexplorer.data.local.MovieEntity
import com.georgiytitov.movieexplorer.data.local.toEntity
import com.georgiytitov.movieexplorer.data.remote.backend.BackendApi
import com.georgiytitov.movieexplorer.data.remote.backend.dto.AddFavoriteDto
import com.georgiytitov.movieexplorer.data.remote.backend.dto.BackendMovieDto
import com.georgiytitov.movieexplorer.data.remote.network.NetworkMonitor
import com.georgiytitov.movieexplorer.support.TestMovies
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultFavoritesRepositoryTest {
    private val dao = mock(MovieDao::class.java)
    private val backend = mock(BackendApi::class.java)
    private val network = mock(NetworkMonitor::class.java)
    private val online = MutableStateFlow(true)

    @Before
    fun setUp() {
        `when`(network.online).thenReturn(online)
        `when`(dao.observeFavorites()).thenReturn(MutableStateFlow<List<MovieEntity>>(emptyList()))
    }

    @Test
    fun synchronizationSendsOnlyTheDifferenceBetweenLocalAndServerIds() {
        runTest {
            `when`(dao.favoriteIds()).thenReturn(listOf(TestMovies.first.id))
            val remoteMovie = BackendMovieDto(
                TestMovies.second.id,
                TestMovies.second.title,
                TestMovies.second.posterPath,
                TestMovies.second.overview,
                TestMovies.second.releaseDate,
                TestMovies.second.rating,
            )
            `when`(backend.favorites(USER_ID)).thenReturn(listOf(remoteMovie))
            val repository = createRepository()
            repository.synchronize()
            verify(backend).addFavorite(USER_ID, AddFavoriteDto(TestMovies.first.id))
            verify(backend).removeFavorite(USER_ID, TestMovies.second.id)
            assertFalse(repository.syncFailed.value)
        }
    }

    @Test
    fun offlineTogglePersistsLocallyAndReportsDeferredSynchronization() {
        runTest {
            online.value = false
            val repository = createRepository()
            repository.toggleFavorite(TestMovies.first)
            runCurrent()
            verify(dao).toggleFavorite(TestMovies.first.toEntity())
            assertTrue(repository.syncFailed.value)
            verifyNoInteractions(backend)
        }
    }

    @Test
    fun failedSynchronizationKeepsLocalFavoritesAndCanBeRetried() {
        runTest {
            `when`(dao.favoriteIds()).thenReturn(listOf(TestMovies.first.id))
            `when`(
                backend.favorites(USER_ID),
            ).thenThrow(IllegalStateException("Backend unavailable"))
                .thenReturn(emptyList())
            val repository = createRepository()
            repository.synchronize()
            assertTrue(repository.syncFailed.value)

            repository.synchronize()
            assertFalse(repository.syncFailed.value)
            verify(backend).addFavorite(USER_ID, AddFavoriteDto(TestMovies.first.id))
        }
    }

    private fun TestScope.createRepository(): DefaultFavoritesRepository {
        return DefaultFavoritesRepository(dao, backend, USER_ID, network, backgroundScope)
    }

    private companion object {
        const val USER_ID = "2a9e33cb-0937-47bb-a13c-06f412ef128f"
    }
}
