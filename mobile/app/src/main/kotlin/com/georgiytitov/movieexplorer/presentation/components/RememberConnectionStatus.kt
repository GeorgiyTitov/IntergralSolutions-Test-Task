package com.georgiytitov.movieexplorer.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.georgiytitov.movieexplorer.di.AppGraph
import com.georgiytitov.movieexplorer.domain.model.DataOrigin
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus

@Composable
fun rememberConnectionStatus(graph: AppGraph): ConnectionStatus {
    val isNetworkAvailable by graph.network.online.collectAsStateWithLifecycle()
    val dataOrigin by graph.movies.origin.collectAsStateWithLifecycle()
    return when {
        !isNetworkAvailable -> ConnectionStatus.OFFLINE
        dataOrigin == DataOrigin.TMDB -> ConnectionStatus.FALLBACK
        dataOrigin == DataOrigin.CACHE -> ConnectionStatus.CACHED
        else -> ConnectionStatus.CONNECTED
    }
}
