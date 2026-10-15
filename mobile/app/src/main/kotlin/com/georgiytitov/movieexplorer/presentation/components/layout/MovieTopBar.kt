package com.georgiytitov.movieexplorer.presentation.components.layout

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieTopBar(
    isDetails: Boolean,
    isFavorites: Boolean,
    goBack: () -> Unit,
    refresh: () -> Unit,
    refreshing: Boolean,
) {
    val title = when {
        isDetails -> stringResource(id = R.string.movie_details)

        isFavorites -> stringResource(id = R.string.favorites)

        else -> {
            stringResource(id = R.string.app_name)
        }
    }
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (isDetails) {
                IconButton(
                    onClick = goBack,
                    content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.back_to_movies),
                        )
                    },
                )
            }
        },
        actions = {
            if (!isDetails && !isFavorites) {
                IconButton(
                    onClick = refresh,
                    enabled = !refreshing,
                    content = {
                        if (refreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(size = Dimensions.REFRESH_INDICATOR_SIZE),
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = stringResource(id = R.string.refresh_movies),
                            )
                        }
                    },
                )
            }
        },
    )
}
