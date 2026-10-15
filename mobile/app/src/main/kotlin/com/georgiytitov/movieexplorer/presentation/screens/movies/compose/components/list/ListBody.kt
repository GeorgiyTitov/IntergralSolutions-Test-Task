package com.georgiytitov.movieexplorer.presentation.screens.movies.compose.components.list

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.presentation.components.feedback.LoadingContent
import com.georgiytitov.movieexplorer.presentation.components.feedback.MessageContent
import com.georgiytitov.movieexplorer.presentation.screens.movies.mvi.MoviesState

@Composable
internal fun ListBody(
    state: MoviesState,
    listState: LazyListState,
    favoriteIds: Set<Long>,
    toggleFavorite: (Movie) -> Unit,
    openMovie: (Long) -> Unit,
    refresh: () -> Unit,
) {
    when {
        state.loading && state.movies.isEmpty() -> LoadingContent()

        state.movies.isEmpty() -> {
            MessageContent(
                title = if (state.error == null) {
                    stringResource(id = R.string.no_movies)
                } else {
                    stringResource(id = R.string.movies_unavailable)
                },
                message = stringResource(id = state.error ?: R.string.empty_movies),
                retry = refresh,
            )
        }

        else -> {
            MovieList(
                state = state,
                listState = listState,
                favoriteIds = favoriteIds,
                toggleFavorite = toggleFavorite,
                openMovie = openMovie,
                refresh = refresh,
            )
        }
    }
}
