package com.georgiytitov.movieexplorer.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.georgiytitov.movieexplorer.presentation.navigation.MovieRoute
import com.georgiytitov.movieexplorer.presentation.resources.Strings

@Composable
fun MovieBottomBar(
    favoritesSelected: Boolean,
    openTab: (String) -> Unit,
) {
    NavigationBar(
        content = {
            listOf(MovieRoute.MOVIES.path, MovieRoute.FAVORITES.path).forEach { route ->
                val favoriteTab = route == MovieRoute.FAVORITES.path
                NavigationBarItem(
                    selected = favoriteTab == favoritesSelected,
                    onClick = { openTab(route) },
                    icon = {
                        Icon(
                            imageVector =
                                if (favoriteTab) Icons.Outlined.FavoriteBorder else {
                                    Icons.Outlined.Movie
                                },
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(
                            text = if (favoriteTab) Strings.FAVORITES else Strings.MOVIES,
                        )
                    },
                )
            }
        },
    )
}
