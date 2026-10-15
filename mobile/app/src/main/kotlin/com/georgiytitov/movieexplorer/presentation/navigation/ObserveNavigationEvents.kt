package com.georgiytitov.movieexplorer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun ObserveNavigationEvents(
    events: Flow<NavigationEvent>,
    onNavigate: (NavigationEvent) -> Unit,
) {
    val owner = LocalLifecycleOwner.current
    val currentHandler by rememberUpdatedState(newValue = onNavigate)
    LaunchedEffect(
        key1 = events,
        key2 = owner,
        block = {
            owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                events.collect { currentHandler(it) }
            }
        },
    )
}
