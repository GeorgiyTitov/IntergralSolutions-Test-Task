package com.georgiytitov.movieexplorer.presentation.screens.movies.compose.effects

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.georgiytitov.movieexplorer.presentation.resources.Pagination
import com.georgiytitov.movieexplorer.presentation.screens.movies.mvi.MoviesState
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
internal fun ObservePagination(
    state: MoviesState,
    listState: LazyListState,
    loadNext: () -> Unit,
) {
    LaunchedEffect(
        key1 = state.tab,
        block = { listState.scrollToItem(index = Pagination.FIRST_ITEM_INDEX) },
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
                        loadNext()
                    }
                }
        },
    )
}
