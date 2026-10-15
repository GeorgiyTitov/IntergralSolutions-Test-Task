package com.georgiytitov.movieexplorer.presentation.screens.details.compose

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
import com.georgiytitov.movieexplorer.presentation.components.connection.rememberConnectionStatus
import com.georgiytitov.movieexplorer.presentation.components.favorites.FavoriteActionButton
import com.georgiytitov.movieexplorer.presentation.components.layout.ScreenFrame
import com.georgiytitov.movieexplorer.presentation.navigation.NavigationEvent
import com.georgiytitov.movieexplorer.presentation.navigation.ObserveNavigationEvents
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus.OFFLINE
import com.georgiytitov.movieexplorer.presentation.screens.details.mvi.DetailsEvent
import com.georgiytitov.movieexplorer.presentation.screens.details.mvi.DetailsViewModel
import com.georgiytitov.movieexplorer.presentation.screens.favorites.compose.FavoritesFeedback
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesEvent
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesViewModel

@Composable
internal fun DetailsScreenView(
    movieId: Long,
    graph: AppGraph,
    onNavigate: (NavigationEvent) -> Unit,
) {
    val details: DetailsViewModel = viewModel(
        factory = viewModelFactory {
            initializer { DetailsViewModel(id = movieId, repository = graph.movies) }
        },
    )
    val state by details.state.collectAsStateWithLifecycle()
    val favorites: FavoritesViewModel = viewModel(
        factory = viewModelFactory {
            initializer { FavoritesViewModel(repository = graph.favorites) }
        },
    )
    val favoriteState by favorites.state.collectAsStateWithLifecycle()
    val connectionStatus = rememberConnectionStatus(graph = graph)
    val isNetworkAvailable = connectionStatus != OFFLINE
    val snackbars = remember(calculation = ::SnackbarHostState)
    var wasOffline by remember(calculation = { mutableStateOf(!isNetworkAvailable) })
    LaunchedEffect(
        key1 = isNetworkAvailable,
        block = {
            if (isNetworkAvailable && wasOffline) {
                details.handleEvent(DetailsEvent.Refresh)
            }
            wasOffline = !isNetworkAvailable
        },
    )
    ObserveNavigationEvents(
        events = details.navigationEvents,
        onNavigate = onNavigate,
    )
    ScreenFrame(
        isDetails = true,
        connectionStatus = connectionStatus,
        snackbars = snackbars,
        feedback = {
            FavoritesFeedback(
                state = favoriteState,
                handleEvent = favorites::handleEvent,
                snackbars = snackbars,
            )
        },
        goBack = { details.handleEvent(DetailsEvent.Back) },
        content = {
            DetailsScreen(
                state = state,
                refresh = { details.handleEvent(DetailsEvent.Refresh) },
                favoriteAction = {
                    state.movie?.let { movie ->
                        FavoriteActionButton(
                            selected = favoriteState.movies.any { it.id == movie.id },
                            onClick = {
                                favorites.handleEvent(FavoritesEvent.Toggle(movie = movie))
                            },
                        )
                    }
                },
            )
        },
    )
}
