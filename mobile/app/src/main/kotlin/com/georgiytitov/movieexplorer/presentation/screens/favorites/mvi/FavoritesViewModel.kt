package com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.domain.repository.FavoritesRepository
import com.georgiytitov.movieexplorer.presentation.navigation.NavigationEvent
import com.georgiytitov.movieexplorer.presentation.state.updateState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class FavoritesViewModel(
    private val repository: FavoritesRepository,
) : ViewModel() {
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
                mutableState.updateState { copy(syncFailed = failed) }
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
            } catch (e: Exception) {
                mutableState.updateState {
                    copy(
                        loading = false,
                        error = R.string.read_favorites_error,
                    )
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

            FavoritesEvent.RetrySync -> {
                viewModelScope.launch { repository.synchronize() }
            }

            is FavoritesEvent.Toggle -> {
                viewModelScope.launch {
                    try {
                        repository.toggleFavorite(event.movie)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        mutableState.updateState { copy(error = R.string.save_favorite_error) }
                    }
                }
            }
        }
    }
}
