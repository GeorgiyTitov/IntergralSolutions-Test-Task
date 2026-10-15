package com.georgiytitov.movieexplorer.presentation.movies

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MovieTab
import com.georgiytitov.movieexplorer.presentation.components.*
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions
import com.georgiytitov.movieexplorer.presentation.resources.Pagination
import com.georgiytitov.movieexplorer.presentation.resources.Strings
import com.georgiytitov.movieexplorer.presentation.resources.title
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun MoviesScreen(
    state: MoviesState,
    handleEvent: (MoviesEvent) -> Unit,
    favoriteIds: Set<Long>,
    toggleFavorite: (Movie) -> Unit,
    openMovie: (Long) -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(
        key1 = state.tab,
        block = { listState.scrollToItem(index = 0) },
    )
    LaunchedEffect(
        key1 = listState,
        key2 = state,
        block = {
            snapshotFlow {
                listState.layoutInfo.visibleItemsInfo
                    .lastOrNull()
                    ?.index ?: Pagination.NO_VISIBLE_ITEM_INDEX
            }.distinctUntilChanged()
                .collect { last ->
                    if (state.movies.isNotEmpty() &&
                        last >= state.movies.size - Pagination.PREFETCH_DISTANCE
                    ) {
                        handleEvent(MoviesEvent.LoadNext)
                    }
                }
        },
    )
    Column(
        modifier = Modifier
            .fillMaxSize(),
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(state = rememberScrollState())
                    .padding(horizontal = Dimensions.LARGE_SPACING),
                horizontalArrangement = Arrangement.spacedBy(space = Dimensions.SMALL_SPACING),
                content = {
                    MovieTab.entries.forEach { tab ->
                        FilterChip(
                            selected = state.tab == tab,
                            onClick = { handleEvent(MoviesEvent.SelectTab(tab = tab)) },
                            label = {
                                Text(
                                    text = tab.title,
                                )
                            },
                        )
                    }
                },
            )
            when {
                state.loading && state.movies.isEmpty() -> LoadingContent()

                state.movies.isEmpty() -> {
                    MessageContent(
                        title =
                            if (state.error == null) {
                                Strings.NO_MOVIES
                            } else {
                                Strings.MOVIES_UNAVAILABLE
                            },
                        message = state.error ?: Strings.EMPTY_MOVIES,
                        retry = { handleEvent(MoviesEvent.Refresh) },
                    )
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(all = Dimensions.LARGE_SPACING),
                        verticalArrangement =
                            Arrangement.spacedBy(
                                space = Dimensions.MEDIUM_SPACING,
                            ),
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
                                    if (state.loadingMore || state.loading) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(all = Dimensions.LARGE_SPACING),
                                            contentAlignment = Alignment.Center,
                                            content = { CircularProgressIndicator() },
                                        )
                                    }
                                    if (state.error != null) {
                                        MessageContent(
                                            title = Strings.LOAD_MORE_ERROR,
                                            message = state.error,
                                            retry = { handleEvent(MoviesEvent.Refresh) },
                                        )
                                    }
                                },
                            )
                        },
                    )
                }
            }
        },
    )
}
