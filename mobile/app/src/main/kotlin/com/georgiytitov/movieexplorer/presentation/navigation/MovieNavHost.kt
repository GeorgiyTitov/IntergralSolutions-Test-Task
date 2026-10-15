package com.georgiytitov.movieexplorer.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.georgiytitov.movieexplorer.di.AppGraph
import com.georgiytitov.movieexplorer.presentation.components.layout.MovieBottomBar
import com.georgiytitov.movieexplorer.presentation.navigation.MovieRoute.DETAILS
import com.georgiytitov.movieexplorer.presentation.navigation.MovieRoute.FAVORITES
import com.georgiytitov.movieexplorer.presentation.navigation.MovieRoute.MOVIES
import com.georgiytitov.movieexplorer.presentation.screens.details.compose.DetailsScreenView
import com.georgiytitov.movieexplorer.presentation.screens.favorites.compose.FavoritesScreenView
import com.georgiytitov.movieexplorer.presentation.screens.movies.compose.MoviesScreenView

@Composable
internal fun MovieNavHost(graph: AppGraph) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.route
    val navigate: (NavigationEvent) -> Unit = { event ->
        when (event) {
            is NavigationEvent.OpenMovie -> {
                nav.navigate(DETAILS.withMovieId(movieId = event.movieId))
            }

            NavigationEvent.Back -> nav.popBackStack()
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets(),
        bottomBar = {
            if (currentRoute != DETAILS.path) {
                MovieBottomBar(
                    favoritesSelected = currentRoute == FAVORITES.path,
                    openTab = { route ->
                        nav.navigate(route) {
                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
        content = { padding ->
            NavHost(
                navController = nav,
                startDestination = MOVIES.path,
                modifier = Modifier
                    .padding(paddingValues = padding)
                    .consumeWindowInsets(paddingValues = padding),
                enterTransition = { MovieTransitions.enter },
                exitTransition = { MovieTransitions.exit },
                popEnterTransition = { MovieTransitions.popEnter },
                popExitTransition = { MovieTransitions.popExit },
                builder = {
                    composable(
                        route = MOVIES.path,
                        content = { MoviesScreenView(graph = graph, onNavigate = navigate) },
                    )
                    composable(
                        route = FAVORITES.path,
                        content = { FavoritesScreenView(graph = graph, onNavigate = navigate) },
                    )
                    composable(
                        route = DETAILS.path,
                        arguments = listOf(
                            navArgument(MovieRoute.MOVIE_ID) { type = NavType.LongType },
                        ),
                        content = { detailsEntry ->
                            DetailsScreenView(
                                movieId = requireNotNull(detailsEntry.arguments)
                                    .getLong(MovieRoute.MOVIE_ID),
                                graph = graph,
                                onNavigate = navigate,
                            )
                        },
                    )
                },
            )
        },
    )
}
