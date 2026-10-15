package com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.domain.repository.FavoritesRepository
import com.georgiytitov.movieexplorer.presentation.navigation.NavigationEvent
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesSyncStatus.FAILED
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesSyncStatus.IDLE
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesSyncStatus.SYNCED
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesSyncStatus.SYNCING
import com.georgiytitov.movieexplorer.presentation.state.updateState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class FavoritesViewModel(private val repository: FavoritesRepository) : ViewModel() {
    private val navigation = Channel<NavigationEvent>(Channel.BUFFERED)
    val navigationEvents = navigation.receiveAsFlow()
    private val mutableState = MutableStateFlow(FavoritesState())
    val state = mutableState.asStateFlow()

    init {
        observeSynchronization()
        observeFavorites()
    }

    private fun observeSynchronization() {
        viewModelScope.launch {
            repository.syncFailed.collect { failed ->
                mutableState.updateState {
                    if (syncStatus == SYNCING || (syncStatus == SYNCED && !failed)) {
                        this
                    } else {
                        copy(
                            syncStatus = if (failed) {
                                FAILED
                            } else {
                                IDLE
                            },
                        )
                    }
                }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            try {
                repository.favorites.collect { movies ->
                    mutableState.updateState { copy(movies = movies, loading = false) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                mutableState.updateState {
                    copy(loading = false, error = R.string.read_favorites_error)
                }
            }
        }
    }

    fun handleEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.OpenMovie -> {
                viewModelScope.launch {
                    navigation.send(NavigationEvent.OpenMovie(movieId = event.movieId))
                }
            }

            FavoritesEvent.DismissError -> {
                mutableState.updateState { copy(error = null) }
            }

            FavoritesEvent.DismissSyncStatus -> {
                mutableState.updateState { copy(syncStatus = IDLE) }
            }

            FavoritesEvent.RetrySync -> {
                retrySynchronization()
            }

            is FavoritesEvent.Toggle -> {
                viewModelScope.launch {
                    try {
                        repository.toggleFavorite(event.movie)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        mutableState.updateState { copy(error = R.string.save_favorite_error) }
                    }
                }
            }
        }
    }

    private fun retrySynchronization() {
        if (state.value.syncStatus == SYNCING) {
            return
        }
        mutableState.updateState { copy(syncStatus = SYNCING) }
        viewModelScope.launch {
            val synchronization = async { repository.synchronize() }
            delay(MINIMUM_SYNC_PROGRESS_MILLIS)
            synchronization.await()
            mutableState.updateState {
                copy(
                    syncStatus = if (repository.syncFailed.value) {
                        FAILED
                    } else {
                        SYNCED
                    },
                )
            }
        }
    }

    private companion object {
        const val MINIMUM_SYNC_PROGRESS_MILLIS = 600L
    }
}
