package com.georgiytitov.movieexplorer.presentation.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.georgiytitov.movieexplorer.presentation.components.*
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions
import com.georgiytitov.movieexplorer.presentation.resources.Strings

@Composable
fun DetailsScreen(
    state: DetailsState,
    refresh: () -> Unit,
    favoriteAction: @Composable () -> Unit = {},
) {
    val movie = state.movie
    when {
        movie == null && state.loading -> LoadingContent()

        movie == null -> {
            MessageContent(
                title = Strings.DETAILS_UNAVAILABLE,
                message = state.error ?: Strings.MOVIE_NOT_FOUND,
                retry = refresh,
            )
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
                    .padding(all = Dimensions.SCREEN_PADDING),
                verticalArrangement = Arrangement.spacedBy(space = Dimensions.SECTION_SPACING),
                content = {
                    if (state.loading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth(),
                        )
                    }
                    if (state.error != null) {
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Poster(
                        movie = movie,
                        modifier = Modifier
                            .width(width = Dimensions.DETAIL_POSTER_WIDTH)
                            .height(height = Dimensions.DETAIL_POSTER_HEIGHT)
                            .align(alignment = Alignment.CenterHorizontally)
                            .clip(
                                shape =
                                    RoundedCornerShape(
                                        size = Dimensions.DETAIL_POSTER_CORNER_RADIUS,
                                    ),
                            ),
                    )
                    Text(
                        text =
                            movie.title.ifBlank { Strings.UNTITLED },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Text(
                                text =
                                    movie.year.ifBlank { Strings.RELEASE_DATE_TBA },
                                style = MaterialTheme.typography.titleMedium,
                            )
                            SuggestionChip(
                                onClick = {},
                                label = {
                                    Text(
                                        text = Strings.ratingOutOfTen(value = movie.rating),
                                    )
                                },
                            )
                        },
                    )
                    favoriteAction()
                    Text(
                        text = Strings.OVERVIEW,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text =
                            movie.overview.ifBlank { Strings.NO_DESCRIPTION },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = Strings.TMDB_ATTRIBUTION,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
        }
    }
}
