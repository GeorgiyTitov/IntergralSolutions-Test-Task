package com.georgiytitov.movieexplorer.presentation.screens.favorites.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.presentation.components.favorites.FavoriteButton
import com.georgiytitov.movieexplorer.presentation.components.feedback.LoadingContent
import com.georgiytitov.movieexplorer.presentation.components.feedback.MessageContent
import com.georgiytitov.movieexplorer.presentation.components.movies.MovieCard
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesEvent
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesState

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
                title = stringResource(id = R.string.empty_favorites_title),
                message = stringResource(id = R.string.empty_favorites_message),
            )
        }

        else -> {
            LazyColumn(
                contentPadding = PaddingValues(all = Dimensions.LARGE_SPACING),
                verticalArrangement = Arrangement.spacedBy(space = Dimensions.MEDIUM_SPACING),
                content = {
                    items(
                        items = state.movies,
                        key = Movie::id,
                        itemContent = { movie ->
                            MovieCard(
                                movie = movie,
                                onClick = { openMovie(movie.id) },
                                trailing = {
                                    FavoriteButton(
                                        selected = true,
                                        toggle = {
                                            handleEvent(FavoritesEvent.Toggle(movie = movie))
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
