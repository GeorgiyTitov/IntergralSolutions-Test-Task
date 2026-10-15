package com.georgiytitov.movieexplorer.presentation

import androidx.compose.runtime.Composable
import com.georgiytitov.movieexplorer.di.AppGraph
import com.georgiytitov.movieexplorer.presentation.navigation.MovieNavHost

@Composable
internal fun MovieApp(graph: AppGraph) {
    MovieNavHost(graph = graph)
}
