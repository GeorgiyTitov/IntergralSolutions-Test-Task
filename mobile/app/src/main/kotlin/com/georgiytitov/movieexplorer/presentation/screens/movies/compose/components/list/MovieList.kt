package com.georgiytitov.movieexplorer.presentation.screens.movies.compose.components.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.presentation.components.favorites.FavoriteButton
import com.georgiytitov.movieexplorer.presentation.components.movies.MovieCard
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions
import com.georgiytitov.movieexplorer.presentation.screens.movies.mvi.MoviesState

@Composable
internal fun MovieList(
    state: MoviesState,
    listState: LazyListState,
    favoriteIds: Set<Long>,
    toggleFavorite: (Movie) -> Unit,
    openMovie: (Long) -> Unit,
    refresh: () -> Unit,
) {
    LazyColumn(
        state = listState,
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
                                selected = movie.id in favoriteIds,
                                toggle = { toggleFavorite(movie) },
                            )
                        },
                    )
                },
            )
            item(
                content = {
                    MovieListFooter(
                        loading = state.loadingMore || state.loading,
                        error = state.error,
                        refresh = refresh,
                    )
                },
            )
        },
    )
}
