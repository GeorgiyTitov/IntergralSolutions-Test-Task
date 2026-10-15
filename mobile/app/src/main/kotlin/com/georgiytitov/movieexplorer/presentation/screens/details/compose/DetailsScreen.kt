package com.georgiytitov.movieexplorer.presentation.screens.details.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.presentation.components.feedback.LoadingContent
import com.georgiytitov.movieexplorer.presentation.components.feedback.MessageContent
import com.georgiytitov.movieexplorer.presentation.components.movies.Poster
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions
import com.georgiytitov.movieexplorer.presentation.screens.details.mvi.DetailsState

@Composable
fun DetailsScreen(
    state: DetailsState,
    refresh: () -> Unit,
    favoriteAction: @Composable () -> Unit = {},
) {
    when (val movie = state.movie) {
        null -> {
            if (state.loading) {
                LoadingContent()
            } else {
                MessageContent(
                    title = stringResource(id = R.string.details_unavailable),
                    message = stringResource(id = state.error ?: R.string.movie_not_found),
                    retry = refresh,
                )
            }
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
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    if (state.error != null) {
                        Text(
                            text = stringResource(id = state.error),
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
                                shape = RoundedCornerShape(
                                    size = Dimensions.DETAIL_POSTER_CORNER_RADIUS,
                                ),
                            ),
                    )
                    Text(
                        text = movie.title.ifBlank { stringResource(id = R.string.untitled) },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Text(
                                text = movie.year.ifBlank {
                                    stringResource(id = R.string.release_date_tba)
                                },
                                style = MaterialTheme.typography.titleMedium,
                            )
                            SuggestionChip(
                                onClick = {},
                                label = {
                                    Text(
                                        text = stringResource(
                                            id = R.string.rating_out_of_ten,
                                            formatArgs = arrayOf(movie.rating),
                                        ),
                                    )
                                },
                            )
                        },
                    )
                    favoriteAction()
                    Text(
                        text = stringResource(id = R.string.overview),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text =
                            movie.overview.ifBlank { stringResource(id = R.string.no_description) },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stringResource(id = R.string.tmdb_attribution),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
        }
    }
}
