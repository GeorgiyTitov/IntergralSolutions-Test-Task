package com.georgiytitov.movieexplorer.presentation.components.movies

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.domain.model.Movie

@Composable
fun Poster(
    movie: Movie,
    modifier: Modifier,
) {
    var failed by remember(
        key1 = movie.posterUrl,
        calculation = { mutableStateOf(false) },
    )
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        content = {
            if (movie.posterUrl == null) {
                Box(
                    contentAlignment = Alignment.Center,
                    content = {
                        Text(
                            text = stringResource(id = R.string.no_poster),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                )
            } else {
                Box(
                    content = {
                        AsyncImage(
                            model = movie.posterUrl,
                            contentDescription = stringResource(
                                id = R.string.poster_description,
                                formatArgs = arrayOf(movie.title),
                            ),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            onError = { failed = true },
                            onSuccess = { failed = false },
                        )
                        if (failed) {
                            Text(
                                text = stringResource(id = R.string.poster_unavailable),
                                modifier = Modifier.align(alignment = Alignment.Center),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    },
                )
            }
        },
    )
}
