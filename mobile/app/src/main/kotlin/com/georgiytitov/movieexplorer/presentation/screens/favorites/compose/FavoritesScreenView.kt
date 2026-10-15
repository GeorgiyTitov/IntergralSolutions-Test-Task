package com.georgiytitov.movieexplorer.presentation.screens.favorites.compose

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.georgiytitov.movieexplorer.di.AppGraph
import com.georgiytitov.movieexplorer.presentation.components.connection.rememberConnectionStatus
import com.georgiytitov.movieexplorer.presentation.components.layout.ScreenFrame
import com.georgiytitov.movieexplorer.presentation.navigation.NavigationEvent
import com.georgiytitov.movieexplorer.presentation.navigation.ObserveNavigationEvents
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesEvent
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesViewModel

@Composable
internal fun FavoritesScreenView(
    graph: AppGraph,
    onNavigate: (NavigationEvent) -> Unit,
) {
    val favorites: FavoritesViewModel = viewModel(
        factory = viewModelFactory {
            initializer { FavoritesViewModel(repository = graph.favorites) }
        },
    )
    val favoriteState by favorites.state.collectAsStateWithLifecycle()
    val connectionStatus = rememberConnectionStatus(graph = graph)
    val snackbars = remember(calculation = ::SnackbarHostState)
    ObserveNavigationEvents(
        events = favorites.navigationEvents,
        onNavigate = onNavigate,
    )
    ScreenFrame(
        isFavorites = true,
        connectionStatus = connectionStatus,
        snackbars = snackbars,
        feedback = {
            FavoritesFeedback(
                state = favoriteState,
                handleEvent = favorites::handleEvent,
                snackbars = snackbars,
            )
        },
        content = {
            FavoritesScreen(
                state = favoriteState,
                handleEvent = favorites::handleEvent,
                openMovie = { favorites.handleEvent(FavoritesEvent.OpenMovie(movieId = it)) },
            )
        },
    )
}
