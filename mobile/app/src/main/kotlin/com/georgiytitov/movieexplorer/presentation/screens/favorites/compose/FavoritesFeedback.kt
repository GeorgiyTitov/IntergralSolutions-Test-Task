package com.georgiytitov.movieexplorer.presentation.screens.favorites.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesEvent
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesState

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
    if (state.syncFailed) {
        Snackbar(
            modifier = Modifier
                .padding(
                    horizontal = Dimensions.LARGE_SPACING,
                    vertical = Dimensions.SMALL_SPACING,
                ),
            action = {
                TextButton(
                    onClick = { handleEvent(FavoritesEvent.RetrySync) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.inversePrimary,
                    ),
                    content = { Text(text = stringResource(id = R.string.try_again)) },
                )
            },
            content = { Text(text = stringResource(id = R.string.sync_favorites_error)) },
        )
    }
}
