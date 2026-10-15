package com.georgiytitov.movieexplorer.presentation.screens.movies.compose.effects

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalResources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun MovieFeedback(
    events: Flow<Int>,
    snackbars: SnackbarHostState,
) {
    val resources = LocalResources.current
    LaunchedEffect(
        key1 = events,
        key2 = snackbars,
        key3 = resources,
        block = {
            events.collectLatest { message ->
                snackbars.showSnackbar(message = resources.getString(message))
            }
        },
    )
}
