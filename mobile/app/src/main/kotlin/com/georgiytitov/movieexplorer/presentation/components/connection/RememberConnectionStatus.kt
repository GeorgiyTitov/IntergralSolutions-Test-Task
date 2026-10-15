package com.georgiytitov.movieexplorer.presentation.components.connection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.georgiytitov.movieexplorer.di.AppGraph
import com.georgiytitov.movieexplorer.domain.model.DataOrigin.BACKEND
import com.georgiytitov.movieexplorer.domain.model.DataOrigin.CACHE
import com.georgiytitov.movieexplorer.domain.model.DataOrigin.TMDB
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus.CACHED
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus.CONNECTED
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus.FALLBACK_TMDB
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus.OFFLINE

@Composable
internal fun rememberConnectionStatus(graph: AppGraph): ConnectionStatus {
    val isNetworkAvailable by graph.network.online.collectAsStateWithLifecycle()
    val dataOrigin by graph.movies.origin.collectAsStateWithLifecycle()
    if (!isNetworkAvailable) {
        return OFFLINE
    }
    return when (dataOrigin) {
        TMDB -> FALLBACK_TMDB
        CACHE -> CACHED
        BACKEND -> CONNECTED
    }
}
