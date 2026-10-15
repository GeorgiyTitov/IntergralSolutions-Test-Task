package com.georgiytitov.movieexplorer.presentation.screens.movies.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.domain.model.DataOrigin.CACHE
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MoviePage
import com.georgiytitov.movieexplorer.domain.model.MoviePage.Companion.FIRST_PAGE
import com.georgiytitov.movieexplorer.domain.model.MoviePage.Companion.NEXT_PAGE_STEP
import com.georgiytitov.movieexplorer.domain.model.MovieTab
import com.georgiytitov.movieexplorer.domain.repository.MovieRepository
import com.georgiytitov.movieexplorer.presentation.navigation.NavigationEvent
import com.georgiytitov.movieexplorer.presentation.state.updateState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class MoviesViewModel(
    private val repository: MovieRepository,
) : ViewModel() {
    private val navigation = Channel<NavigationEvent>(Channel.BUFFERED)
    val navigationEvents = navigation.receiveAsFlow()
    private val feedback = Channel<Int>(Channel.BUFFERED)
    val feedbackEvents = feedback.receiveAsFlow()
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
                if (state.value.tab == event.tab) {
                    return
                }
                request?.cancel()
                mutableState.value = MoviesState(tab = event.tab)
                load(refresh = true)
            }

            MoviesEvent.Refresh -> {
                request?.cancel()
                load(refresh = true, notifyResult = true)
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

    private fun load(
        refresh: Boolean,
        notifyResult: Boolean = false,
    ) {
        val tab = state.value.tab
        val page = if (refresh) {
            FIRST_PAGE
        } else {
            state.value.nextPage
        }
        mutableState.updateState { copy(loading = refresh, loadingMore = !refresh, error = null) }
        request = viewModelScope.launch {
            loadMovies(tab = tab, page = page, refresh = refresh, notifyResult = notifyResult)
        }
    }

    private suspend fun loadMovies(
        tab: MovieTab,
        page: Int,
        refresh: Boolean,
        notifyResult: Boolean,
    ) {
        try {
            if (refresh) {
                showCachedMovies(tab = tab)
            }
            val result = repository.movies(tab, page)
            showMovies(result = result, refresh = refresh)
            if (notifyResult) {
                feedback.send(
                    if (result.origin == CACHE) {
                        R.string.refresh_cached_movies
                    } else {
                        R.string.movies_updated
                    },
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            mutableState.updateState {
                copy(
                    loading = false,
                    loadingMore = false,
                    error = R.string.load_movies_error,
                )
            }
            if (notifyResult) {
                feedback.send(R.string.load_movies_error)
            }
        }
    }

    private suspend fun showCachedMovies(tab: MovieTab) {
        if (state.value.movies.isEmpty()) {
            repository.cachedMovies(tab, FIRST_PAGE)?.let { cached ->
                mutableState.updateState { copy(movies = cached.movies) }
            }
        }
    }

    private fun showMovies(
        result: MoviePage,
        refresh: Boolean,
    ) {
        mutableState.updateState {
            val loadedMovies = if (refresh) {
                result.movies
            } else {
                movies + result.movies
            }
            copy(
                movies = loadedMovies.distinctBy(Movie::id),
                loading = false,
                loadingMore = false,
                nextPage = result.page + NEXT_PAGE_STEP,
                totalPages = result.totalPages,
            )
        }
    }
}
