package com.georgiytitov.movieexplorer.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.georgiytitov.movieexplorer.domain.model.Movie

@Composable
fun Poster(movie: Movie, modifier: Modifier) {
    Surface(modifier, color = MaterialTheme.colorScheme.surfaceContainerHighest) {
        if (movie.posterUrl == null) Box(contentAlignment = Alignment.Center) { Text("No poster", style = MaterialTheme.typography.labelSmall) }
        else AsyncImage(model = movie.posterUrl, contentDescription = "Poster for ${movie.title}", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
    }
}
