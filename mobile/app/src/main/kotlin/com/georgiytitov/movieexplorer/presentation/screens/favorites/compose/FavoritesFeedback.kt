package com.georgiytitov.movieexplorer.presentation.screens.favorites.compose

import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesEvent
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesState
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesSyncStatus.FAILED
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesSyncStatus.IDLE
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesSyncStatus.SYNCED
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesSyncStatus.SYNCING

@Composable
fun FavoritesFeedback(
    state: FavoritesState,
    handleEvent: (FavoritesEvent) -> Unit,
    snackbars: SnackbarHostState,
) {
    val errorMessage = state.error?.let { error -> stringResource(id = error) }
    LaunchedEffect(
        key1 = errorMessage,
        block = {
            errorMessage?.let { error ->
                snackbars.showSnackbar(message = error)
                handleEvent(FavoritesEvent.DismissError)
            }
        },
    )
    val syncMessage = when (state.syncStatus) {
        IDLE -> null
        SYNCING -> stringResource(id = R.string.syncing_favorites)
        SYNCED -> stringResource(id = R.string.favorites_synced)
        FAILED -> stringResource(id = R.string.sync_favorites_error)
    }
    val retryLabel = stringResource(id = R.string.try_again)
    LaunchedEffect(
        key1 = state.syncStatus,
        block = {
            if (syncMessage != null) {
                val result = snackbars.showSnackbar(
                    message = syncMessage,
                    actionLabel = if (state.syncStatus == FAILED) {
                        retryLabel
                    } else {
                        null
                    },
                    duration = if (state.syncStatus == SYNCING) {
                        Indefinite
                    } else {
                        Short
                    },
                )
                handleEvent(FavoritesEvent.DismissSyncStatus)
                if (result == ActionPerformed) {
                    handleEvent(FavoritesEvent.RetrySync)
                }
            }
        },
    )
}
