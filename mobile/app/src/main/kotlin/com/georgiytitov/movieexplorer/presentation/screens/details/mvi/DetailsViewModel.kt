package com.georgiytitov.movieexplorer.presentation.screens.details.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgiytitov.movieexplorer.R
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

internal class DetailsViewModel(
    private val id: Long,
    private val repository: MovieRepository,
) : ViewModel() {
    private val navigation = Channel<NavigationEvent>(Channel.BUFFERED)
    val navigationEvents = navigation.receiveAsFlow()
    private val mutableState = MutableStateFlow(DetailsState())
    val state = mutableState.asStateFlow()
    private var request: Job? = null

    init {
        handleEvent(DetailsEvent.Refresh)
    }

    fun handleEvent(event: DetailsEvent) {
        if (event == DetailsEvent.Back) {
            viewModelScope.launch { navigation.send(NavigationEvent.Back) }
            return
        }
        request?.cancel()
        request = viewModelScope.launch {
            mutableState.updateState { copy(loading = true, error = null) }
            try {
                if (state.value.movie == null) {
                    repository.cachedMovie(id)?.let { movie ->
                        mutableState.updateState { copy(movie = movie) }
                    }
                }
                val (movie, _) = repository.movie(id)
                mutableState.value = DetailsState(movie = movie, loading = false)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                mutableState.updateState {
                    copy(loading = false, error = R.string.load_details_error)
                }
            }
        }
    }
}
