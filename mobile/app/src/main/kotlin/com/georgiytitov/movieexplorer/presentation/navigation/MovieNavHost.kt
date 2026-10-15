package com.georgiytitov.movieexplorer.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.georgiytitov.movieexplorer.di.AppGraph
import com.georgiytitov.movieexplorer.presentation.components.MovieBottomBar
import com.georgiytitov.movieexplorer.presentation.details.DetailsScreenView
import com.georgiytitov.movieexplorer.presentation.favorites.FavoritesScreenView
import com.georgiytitov.movieexplorer.presentation.movies.MoviesScreenView
import com.georgiytitov.movieexplorer.presentation.resources.Motion

@Composable
fun MovieNavHost(graph: AppGraph) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.route
    val navigate: (NavigationEvent) -> Unit = { event ->
        when (event) {
            is NavigationEvent.OpenMovie -> {
                nav.navigate(
                    MovieRoute.DETAILS.withMovieId(movieId = event.movieId),
                )
            }

            NavigationEvent.Back -> nav.popBackStack()
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets(),
        bottomBar = {
            if (currentRoute != MovieRoute.DETAILS.path) {
                MovieBottomBar(
                    favoritesSelected = currentRoute == MovieRoute.FAVORITES.path,
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
                startDestination = MovieRoute.MOVIES.path,
                modifier = Modifier
                    .padding(paddingValues = padding)
                    .consumeWindowInsets(paddingValues = padding),
                enterTransition = {
                    slideInHorizontally(
                        animationSpec = tween(durationMillis = Motion.ENTER_DURATION_MILLIS),
                        initialOffsetX = { it / Motion.HORIZONTAL_OFFSET_DIVISOR },
                    ) + fadeIn(animationSpec = tween(durationMillis = Motion.ENTER_DURATION_MILLIS))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(durationMillis = Motion.EXIT_DURATION_MILLIS))
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(durationMillis = Motion.ENTER_DURATION_MILLIS))
                },
                popExitTransition = {
                    slideOutHorizontally(
                        animationSpec = tween(durationMillis = Motion.ENTER_DURATION_MILLIS),
                        targetOffsetX = { it / Motion.HORIZONTAL_OFFSET_DIVISOR },
                    ) + fadeOut(animationSpec = tween(durationMillis = Motion.EXIT_DURATION_MILLIS))
                },
                builder = {
                    composable(
                        route = MovieRoute.MOVIES.path,
                        content = {
                            MoviesScreenView(
                                graph = graph,
                                onNavigate = navigate,
                            )
                        },
                    )
                    composable(
                        route = MovieRoute.FAVORITES.path,
                        content = {
                            FavoritesScreenView(
                                graph = graph,
                                onNavigate = navigate,
                            )
                        },
                    )
                    composable(
                        route = MovieRoute.DETAILS.path,
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
