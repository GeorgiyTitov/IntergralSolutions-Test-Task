package com.georgiytitov.movieexplorer.presentation.components.layout

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.presentation.navigation.MovieRoute.FAVORITES
import com.georgiytitov.movieexplorer.presentation.navigation.MovieRoute.MOVIES

@Composable
fun MovieBottomBar(
    favoritesSelected: Boolean,
    openTab: (String) -> Unit,
) {
    NavigationBar(
        content = {
            listOf(MOVIES.path, FAVORITES.path).forEach { route ->
                val favoriteTab = route == FAVORITES.path
                NavigationBarItem(
                    selected = favoriteTab == favoritesSelected,
                    onClick = { openTab(route) },
                    icon = {
                        Icon(
                            imageVector = if (favoriteTab) {
                                Icons.Outlined.FavoriteBorder
                            } else {
                                Icons.Outlined.Movie
                            },
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(
                            text = if (favoriteTab) {
                                stringResource(id = R.string.favorites)
                            } else {
                                stringResource(id = R.string.movies)
                            },
                        )
                    },
                )
            }
        },
    )
}
