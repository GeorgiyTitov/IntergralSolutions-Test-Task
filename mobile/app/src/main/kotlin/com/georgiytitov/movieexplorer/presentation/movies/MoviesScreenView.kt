package com.georgiytitov.movieexplorer.presentation.movies

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.georgiytitov.movieexplorer.di.AppGraph
import com.georgiytitov.movieexplorer.presentation.components.*
import com.georgiytitov.movieexplorer.presentation.favorites.*
import com.georgiytitov.movieexplorer.presentation.navigation.NavigationEvent
import com.georgiytitov.movieexplorer.presentation.navigation.ObserveNavigationEvents
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus

@Composable
fun MoviesScreenView(
    graph: AppGraph,
    onNavigate: (NavigationEvent) -> Unit,
) {
    val movies: MoviesViewModel = viewModel(
        factory = viewModelFactory { initializer { MoviesViewModel(repository = graph.movies) } },
    )
    val state by movies.state.collectAsStateWithLifecycle()
    val favorites: FavoritesViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                FavoritesViewModel(
                    repository = graph.favorites,
                )
            }
        },
    )
    val favoriteState by favorites.state.collectAsStateWithLifecycle()
    val connectionStatus = rememberConnectionStatus(
        graph = graph,
    )
    val isNetworkAvailable = connectionStatus != ConnectionStatus.OFFLINE
    val snackbars = remember(
        calculation = { SnackbarHostState() },
    )
    LaunchedEffect(
        key1 = favoriteState.error,
        block = {
            favoriteState.error?.let { error ->
                snackbars.showSnackbar(message = error)
                favorites.handleEvent(FavoritesEvent.DismissError)
            }
        },
    )
    var wasOffline by remember(
        calculation = { mutableStateOf(!isNetworkAvailable) },
    )
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
        connectionStatus = connectionStatus,
        snackbars = snackbars,
        refresh = { movies.handleEvent(MoviesEvent.Refresh) },
        content = {
            MoviesScreen(
                state = state,
                handleEvent = movies::handleEvent,
                favoriteIds = favoriteState.movies.map { it.id }.toSet(),
                toggleFavorite = { favorites.handleEvent(FavoritesEvent.Toggle(movie = it)) },
                openMovie = { movies.handleEvent(MoviesEvent.OpenMovie(movieId = it)) },
            )
        },
    )
}
