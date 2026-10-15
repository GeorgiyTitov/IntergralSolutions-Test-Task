package com.georgiytitov.movieexplorer.presentation.details

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
fun DetailsScreenView(
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
