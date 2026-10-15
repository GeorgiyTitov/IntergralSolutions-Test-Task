package com.georgiytitov.movieexplorer.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.presentation.resources.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieTopBar(
    isDetails: Boolean,
    isFavorites: Boolean,
    goBack: () -> Unit,
    refresh: () -> Unit,
) {
    val title =
        when {
            isDetails -> Strings.MOVIE_DETAILS

            isFavorites -> Strings.FAVORITES

            else -> {
                stringResource(
                    id = R.string.app_name,
                )
            }
        }
    TopAppBar(
        title = {
            Text(
                text = title,
            )
        },
        navigationIcon = {
            if (isDetails) {
                IconButton(
                    onClick = goBack,
                    content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = Strings.BACK_TO_MOVIES,
                        )
                    },
                )
            }
        },
        actions = {
            if (!isDetails && !isFavorites) {
                IconButton(
                    onClick = refresh,
                    content = {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = Strings.REFRESH_MOVIES,
                        )
                    },
                )
            }
        },
    )
}
