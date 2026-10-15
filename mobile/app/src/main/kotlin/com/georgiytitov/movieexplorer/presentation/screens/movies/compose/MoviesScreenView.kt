package com.georgiytitov.movieexplorer.presentation.screens.movies.compose

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.georgiytitov.movieexplorer.di.AppGraph
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.presentation.components.connection.rememberConnectionStatus
import com.georgiytitov.movieexplorer.presentation.components.layout.ScreenFrame
import com.georgiytitov.movieexplorer.presentation.navigation.NavigationEvent
import com.georgiytitov.movieexplorer.presentation.navigation.ObserveNavigationEvents
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus.OFFLINE
import com.georgiytitov.movieexplorer.presentation.screens.favorites.compose.FavoritesFeedback
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesEvent
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesViewModel
import com.georgiytitov.movieexplorer.presentation.screens.movies.compose.effects.MovieFeedback
import com.georgiytitov.movieexplorer.presentation.screens.movies.mvi.MoviesEvent
import com.georgiytitov.movieexplorer.presentation.screens.movies.mvi.MoviesViewModel

@Composable
internal fun MoviesScreenView(
    graph: AppGraph,
    onNavigate: (NavigationEvent) -> Unit,
) {
    val movies: MoviesViewModel = viewModel(
        factory = viewModelFactory { initializer { MoviesViewModel(repository = graph.movies) } },
    )
    val state by movies.state.collectAsStateWithLifecycle()
    val favorites: FavoritesViewModel = viewModel(
        factory = viewModelFactory {
            initializer { FavoritesViewModel(repository = graph.favorites) }
        },
    )
    val favoriteState by favorites.state.collectAsStateWithLifecycle()
    val connectionStatus = rememberConnectionStatus(graph = graph)
    val isNetworkAvailable = connectionStatus != OFFLINE
    val snackbars = remember(calculation = ::SnackbarHostState)
    MovieFeedback(
        events = movies.feedbackEvents,
        snackbars = snackbars,
    )
    var wasOffline by remember(calculation = { mutableStateOf(!isNetworkAvailable) })
    LaunchedEffect(
        key1 = isNetworkAvailable,
        block = {
            if (isNetworkAvailable && wasOffline) {
                movies.handleEvent(MoviesEvent.Refresh)
            }
            wasOffline = !isNetworkAvailable
        },
    )
    ObserveNavigationEvents(
        events = movies.navigationEvents,
        onNavigate = onNavigate,
    )
    ScreenFrame(
        refreshing = state.loading && state.movies.isNotEmpty(),
        connectionStatus = connectionStatus,
        snackbars = snackbars,
        feedback = {
            FavoritesFeedback(
                state = favoriteState,
                handleEvent = favorites::handleEvent,
                snackbars = snackbars,
            )
        },
        refresh = { movies.handleEvent(MoviesEvent.Refresh) },
        content = {
            MoviesScreen(
                state = state,
                handleEvent = movies::handleEvent,
                favoriteIds = favoriteState.movies.map(Movie::id).toSet(),
                toggleFavorite = { favorites.handleEvent(FavoritesEvent.Toggle(movie = it)) },
                openMovie = { movies.handleEvent(MoviesEvent.OpenMovie(movieId = it)) },
            )
        },
    )
}
