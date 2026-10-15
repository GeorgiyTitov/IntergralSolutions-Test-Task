package com.georgiytitov.movieexplorer.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions
import com.georgiytitov.movieexplorer.presentation.resources.Strings

private const val TITLE_MAX_LINES = 2
private const val OVERVIEW_MAX_LINES = 2

@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    trailing: @Composable () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(size = Dimensions.CARD_CORNER_RADIUS),
        content = {
            Row(
                modifier = Modifier
                    .padding(all = Dimensions.CARD_PADDING),
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    Poster(
                        movie = movie,
                        modifier = Modifier
                            .width(width = Dimensions.LIST_POSTER_WIDTH)
                            .height(height = Dimensions.LIST_POSTER_HEIGHT)
                            .clip(
                                shape = RoundedCornerShape(
                                    size = Dimensions.POSTER_CORNER_RADIUS,
                                ),
                            ),
                    )
                    Column(
                        modifier = Modifier
                            .weight(weight = 1f)
                            .padding(start = Dimensions.LARGE_SPACING),
                        verticalArrangement =
                            Arrangement.spacedBy(
                                space = Dimensions.SMALL_SPACING,
                            ),
                        content = {
                            Text(
                                text =
                                    movie.title.ifBlank { Strings.UNTITLED },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = TITLE_MAX_LINES,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text =
                                    Strings.movieMetadata(
                                        year = movie.year,
                                        ratingValue = movie.rating,
                                    ),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text =
                                    movie.overview.ifBlank { Strings.DESCRIPTION_COMING_SOON },
                                maxLines = OVERVIEW_MAX_LINES,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                    )
                    trailing()
                },
            )
        },
    )
}
