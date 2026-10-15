package com.georgiytitov.movieexplorer.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.repository.MovieRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailsViewModel(private val id: Long, private val repository: MovieRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(DetailsState())
    val state = mutableState.asStateFlow()
    private var request: Job? = null

    init { accept(DetailsEvent.Refresh) }

    fun accept(event: DetailsEvent) {
        request?.cancel()
        request = viewModelScope.launch {
            mutableState.update { it.copy(loading = true, error = null) }
            try {
                val (movie, _) = repository.movie(id)
                mutableState.value = DetailsState(movie = movie, loading = false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mutableState.update { it.copy(loading = false, error = "Movie details are unavailable. Check your connection and try again.") }
            }
        }
    }
}
