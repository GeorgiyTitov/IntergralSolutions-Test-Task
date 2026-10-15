package com.georgiytitov.movieexplorer.presentation.favorites

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.georgiytitov.movieexplorer.di.AppGraph
import com.georgiytitov.movieexplorer.presentation.components.*
import com.georgiytitov.movieexplorer.presentation.navigation.NavigationEvent
import com.georgiytitov.movieexplorer.presentation.navigation.ObserveNavigationEvents

@Composable
fun FavoritesScreenView(
    graph: AppGraph,
    onNavigate: (NavigationEvent) -> Unit,
) {
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
    ObserveNavigationEvents(
        events = favorites.navigationEvents,
        onNavigate = onNavigate,
    )
    ScreenFrame(
        isFavorites = true,
        connectionStatus = connectionStatus,
        snackbars = snackbars,
        content = {
            FavoritesScreen(
                state = favoriteState,
                handleEvent = favorites::handleEvent,
                openMovie = { favorites.handleEvent(FavoritesEvent.OpenMovie(movieId = it)) },
            )
        },
    )
}
