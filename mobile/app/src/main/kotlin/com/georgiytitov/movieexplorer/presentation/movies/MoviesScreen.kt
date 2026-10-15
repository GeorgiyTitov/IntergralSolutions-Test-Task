package com.georgiytitov.movieexplorer.presentation.movies

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MovieTab
import com.georgiytitov.movieexplorer.presentation.components.*
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun MoviesScreen(state: MoviesState, accept: (MoviesEvent) -> Unit, openMovie: (Long) -> Unit) {
    val listState = rememberLazyListState()
    LaunchedEffect(state.tab) { listState.scrollToItem(0) }
    LaunchedEffect(listState, state.movies.size, state.nextPage, state.loading, state.loadingMore, state.error) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .distinctUntilChanged().collect { last ->
                if (state.movies.isNotEmpty() && last >= state.movies.size - 4) accept(MoviesEvent.LoadNext)
            }
    }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MovieTab.entries.forEach { tab ->
                FilterChip(selected = state.tab == tab, onClick = { accept(MoviesEvent.SelectTab(tab)) }, label = { Text(tab.label) })
            }
        }
        when {
            state.loading && state.movies.isEmpty() -> LoadingContent()
            state.movies.isEmpty() -> MessageContent(
                if (state.error == null) "No movies yet" else "Movies unavailable",
                state.error ?: "Try another tab, or check again later.",
                { accept(MoviesEvent.Refresh) },
            )
            else -> LazyColumn(state = listState, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.movies, key = Movie::id) { movie -> MovieCard(movie, { openMovie(movie.id) }) }
                item {
                    if (state.loadingMore || state.loading) Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    if (state.error != null) MessageContent("Couldn’t load more", state.error, { accept(MoviesEvent.Refresh) })
                }
            }
        }
    }
}
