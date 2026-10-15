package com.georgiytitov.movieexplorer.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus

@Composable
fun ScreenFrame(
    isDetails: Boolean = false,
    isFavorites: Boolean = false,
    connectionStatus: ConnectionStatus,
    snackbars: SnackbarHostState,
    goBack: () -> Unit = {},
    refresh: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            Column(
                content = {
                    MovieTopBar(
                        isDetails = isDetails,
                        isFavorites = isFavorites,
                        goBack = goBack,
                        refresh = refresh,
                    )
                    ConnectionBanner(
                        connectionStatus = connectionStatus,
                    )
                },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbars,
            )
        },
        content = { padding ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues = padding),
                content = content,
            )
        },
    )
}
