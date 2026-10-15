package com.georgiytitov.movieexplorer.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgiytitov.movieexplorer.domain.repository.FavoritesRepository
import com.georgiytitov.movieexplorer.presentation.navigation.NavigationEvent
import com.georgiytitov.movieexplorer.presentation.resources.Strings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FavoritesViewModel(
    private val repository: FavoritesRepository,
) : ViewModel() {
    private val navigation = Channel<NavigationEvent>(Channel.BUFFERED)
    val navigationEvents = navigation.receiveAsFlow()
    private val mutableState = MutableStateFlow(FavoritesState())
    val state = mutableState.asStateFlow()
    private val mutations = Mutex()
    private val selected = mutableSetOf<Long>()

    init {
        viewModelScope.launch {
            try {
                repository.favorites.collect { movies ->
                    mutations.withLock {
                        selected.clear()
                        selected.addAll(movies.map { it.id })
                        mutableState.update { it.copy(movies = movies, loading = false) }
                    }
                }
            } catch (
                e: CancellationException,
            ) {
                throw e
            } catch (
                e: Exception,
            ) {
                mutableState.update {
                    it.copy(
                        loading = false,
                        error = Strings.READ_FAVORITES_ERROR,
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
                mutableState.update { it.copy(error = null) }
            }

            is FavoritesEvent.Toggle -> {
                viewModelScope.launch {
                    mutations.withLock {
                        try {
                            val save = event.movie.id !in selected
                            repository.setFavorite(event.movie, save)
                            if (save) {
                                selected.add(
                                    event.movie.id,
                                )
                            } else {
                                selected.remove(event.movie.id)
                            }
                        } catch (
                            e: CancellationException,
                        ) {
                            throw e
                        } catch (
                            e: Exception,
                        ) {
                            mutableState.update { it.copy(error = Strings.SAVE_FAVORITE_ERROR) }
                        }
                    }
                }
            }
        }
    }
}
