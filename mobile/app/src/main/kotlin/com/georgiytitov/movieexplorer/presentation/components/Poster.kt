package com.georgiytitov.movieexplorer.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.presentation.resources.Strings

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
                            text = Strings.NO_POSTER,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                )
            } else {
                Box(
                    content = {
                        AsyncImage(
                            model = movie.posterUrl,
                            contentDescription = Strings.posterDescription(title = movie.title),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize(),
                            onError = { failed = true },
                            onSuccess = { failed = false },
                        )
                        if (failed) {
                            Text(
                                text = Strings.POSTER_UNAVAILABLE,
                                modifier = Modifier
                                    .align(alignment = Alignment.Center),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    },
                )
            }
        },
    )
}
