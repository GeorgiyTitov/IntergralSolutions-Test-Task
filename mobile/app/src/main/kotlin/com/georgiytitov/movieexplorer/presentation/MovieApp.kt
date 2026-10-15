package com.georgiytitov.movieexplorer.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.georgiytitov.movieexplorer.di.AppGraph
import com.georgiytitov.movieexplorer.presentation.movies.*
import com.georgiytitov.movieexplorer.presentation.details.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieApp(graph: AppGraph) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val isDetails = entry?.destination?.route == "movie/{id}"
    val movies: MoviesViewModel = viewModel(factory = viewModelFactory { initializer { MoviesViewModel(graph.movies) } })
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(if (isDetails) "Movie details" else "Movie Explorer") },
            navigationIcon = {
                if (isDetails) IconButton(onClick = { nav.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back to movies")
                }
            },
            actions = {
                if (!isDetails) IconButton(onClick = { movies.accept(MoviesEvent.Refresh) }) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh movies")
                }
            },
        )
    }) { padding ->
        NavHost(
            nav, startDestination = "browse", modifier = Modifier.padding(padding),
            enterTransition = { slideInHorizontally(tween(240)) { it / 6 } + fadeIn(tween(240)) },
            exitTransition = { fadeOut(tween(140)) },
            popEnterTransition = { fadeIn(tween(240)) },
            popExitTransition = { slideOutHorizontally(tween(240)) { it / 6 } + fadeOut(tween(140)) },
        ) {
            composable("browse") {
                val state by movies.state.collectAsStateWithLifecycle()
                MoviesScreen(state, movies::accept) { nav.navigate("movie/$it") }
            }
            composable("movie/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { detailsEntry ->
                val id = requireNotNull(detailsEntry.arguments).getLong("id")
                val details: DetailsViewModel = viewModel(factory = viewModelFactory { initializer { DetailsViewModel(id, graph.movies) } })
                val state by details.state.collectAsStateWithLifecycle()
                DetailsScreen(state, { details.accept(DetailsEvent.Refresh) })
            }
        }
    }
}
