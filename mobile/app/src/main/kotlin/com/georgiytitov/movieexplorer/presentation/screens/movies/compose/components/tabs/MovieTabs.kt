package com.georgiytitov.movieexplorer.presentation.screens.movies.compose.components.tabs

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.georgiytitov.movieexplorer.domain.model.MovieTab
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions
import com.georgiytitov.movieexplorer.presentation.resources.titleRes

@Composable
internal fun MovieTabs(
    selectedTab: MovieTab,
    selectTab: (MovieTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(state = rememberScrollState())
            .padding(horizontal = Dimensions.LARGE_SPACING),
        horizontalArrangement = Arrangement.spacedBy(space = Dimensions.SMALL_SPACING),
        content = {
            MovieTab.entries.forEach { tab ->
                FilterChip(
                    selected = selectedTab == tab,
                    onClick = { selectTab(tab) },
                    label = { Text(text = stringResource(id = tab.titleRes)) },
                )
            }
        },
    )
}
