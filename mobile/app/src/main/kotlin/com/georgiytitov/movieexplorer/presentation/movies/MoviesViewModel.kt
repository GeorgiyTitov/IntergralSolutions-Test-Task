package com.georgiytitov.movieexplorer.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MovieTab
import com.georgiytitov.movieexplorer.domain.repository.MovieRepository
import com.georgiytitov.movieexplorer.presentation.navigation.NavigationEvent
import com.georgiytitov.movieexplorer.presentation.resources.Strings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val repository: MovieRepository,
) : ViewModel() {
    private val navigation = Channel<NavigationEvent>(Channel.BUFFERED)
    val navigationEvents = navigation.receiveAsFlow()
    private val mutableState = MutableStateFlow(MoviesState())
    val state = mutableState.asStateFlow()
    private var request: Job? = null

    init {
        load(refresh = true)
    }

    fun handleEvent(event: MoviesEvent) {
        when (event) {
            is MoviesEvent.OpenMovie -> {
                viewModelScope.launch {
                    navigation.send(NavigationEvent.OpenMovie(movieId = event.movieId))
                }
            }

            is MoviesEvent.SelectTab -> {
                if (state.value.tab == event.tab) return
                request?.cancel()
                mutableState.value = MoviesState(tab = event.tab)
                load(refresh = true)
            }

            MoviesEvent.Refresh -> {
                request?.cancel()
                load(refresh = true)
            }

            MoviesEvent.LoadNext -> {
                val current = state.value
                if (!current.loading && !current.loadingMore && current.error == null &&
                    current.nextPage <= current.totalPages
                ) {
                    load(refresh = false)
                }
            }
        }
    }

    private fun load(refresh: Boolean) {
        val tab = state.value.tab
        val page = if (refresh) 1 else state.value.nextPage
        mutableState.update { it.copy(loading = refresh, loadingMore = !refresh, error = null) }
        request =
            viewModelScope.launch {
                try {
                    if (refresh &&
                        state.value.movies.isEmpty()
                    ) {
                        repository.cachedMovies(tab, 1)?.let { cached ->
                            mutableState.update { it.copy(movies = cached.movies) }
                        }
                    }
                    val result = repository.movies(tab, page)
                    mutableState.update {
                        it.copy(
                            movies =
                                (if (refresh) result.movies else it.movies + result.movies)
                                    .distinctBy(
                                        Movie::id,
                                    ),
                            loading = false,
                            loadingMore = false,
                            nextPage = result.page + 1,
                            totalPages = result.totalPages,
                        )
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    mutableState.update {
                        it.copy(
                            loading = false,
                            loadingMore = false,
                            error = Strings.LOAD_MOVIES_ERROR,
                        )
                    }
                }
            }
    }
}
