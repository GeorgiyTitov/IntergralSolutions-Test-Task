package com.georgiytitov.movieexplorer.presentation.screens.movies.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.presentation.screens.movies.compose.components.list.ListBody
import com.georgiytitov.movieexplorer.presentation.screens.movies.compose.components.tabs.MovieTabs
import com.georgiytitov.movieexplorer.presentation.screens.movies.compose.effects.ObservePagination
import com.georgiytitov.movieexplorer.presentation.screens.movies.mvi.MoviesEvent
import com.georgiytitov.movieexplorer.presentation.screens.movies.mvi.MoviesState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    state: MoviesState,
    handleEvent: (MoviesEvent) -> Unit,
    favoriteIds: Set<Long>,
    toggleFavorite: (Movie) -> Unit,
    openMovie: (Long) -> Unit,
) {
    val listState = rememberLazyListState()
    ObservePagination(
        state = state,
        listState = listState,
        loadNext = { handleEvent(MoviesEvent.LoadNext) },
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        content = {
            MovieTabs(
                selectedTab = state.tab,
                selectTab = { tab -> handleEvent(MoviesEvent.SelectTab(tab = tab)) },
            )
            PullToRefreshBox(
                isRefreshing = state.loading && state.movies.isNotEmpty(),
                onRefresh = { handleEvent(MoviesEvent.Refresh) },
                modifier = Modifier.fillMaxSize(),
                content = {
                    ListBody(
                        state = state,
                        listState = listState,
                        favoriteIds = favoriteIds,
                        toggleFavorite = toggleFavorite,
                        openMovie = openMovie,
                        refresh = { handleEvent(MoviesEvent.Refresh) },
                    )
                },
            )
        },
    )
}
