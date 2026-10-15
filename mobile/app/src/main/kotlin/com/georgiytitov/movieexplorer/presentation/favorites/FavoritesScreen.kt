package com.georgiytitov.movieexplorer.presentation.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.georgiytitov.movieexplorer.presentation.components.*
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions
import com.georgiytitov.movieexplorer.presentation.resources.Strings

@Composable
fun FavoritesScreen(
    state: FavoritesState,
    handleEvent: (FavoritesEvent) -> Unit,
    openMovie: (Long) -> Unit,
) {
    when {
        state.loading -> LoadingContent()

        state.movies.isEmpty() -> {
            MessageContent(
                title = Strings.EMPTY_FAVORITES_TITLE,
                message = Strings.EMPTY_FAVORITES_MESSAGE,
            )
        }

        else -> {
            LazyColumn(
                contentPadding = PaddingValues(all = Dimensions.LARGE_SPACING),
                verticalArrangement = Arrangement.spacedBy(space = Dimensions.MEDIUM_SPACING),
                content = {
                    items(
                        items = state.movies,
                        key = { it.id },
                        itemContent = { movie ->
                            MovieCard(
                                movie = movie,
                                onClick = { openMovie(movie.id) },
                                trailing = {
                                    FavoriteButton(
                                        selected = true,
                                        toggle = {
                                            handleEvent(
                                                FavoritesEvent.Toggle(movie = movie),
                                            )
                                        },
                                    )
                                },
                            )
                        },
                    )
                },
            )
        }
    }
}
