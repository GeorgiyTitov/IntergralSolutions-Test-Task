package com.georgiytitov.movieexplorer.presentation.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.georgiytitov.movieexplorer.presentation.components.*
import java.util.Locale

@Composable
fun DetailsScreen(state: DetailsState, refresh: () -> Unit, favoriteAction: @Composable () -> Unit = {}) {
    val movie = state.movie
    when {
        movie == null && state.loading -> LoadingContent()
        movie == null -> MessageContent("Details unavailable", state.error ?: "This movie could not be found.", refresh)
        else -> Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
            if (state.error != null) Text(state.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            Poster(movie, Modifier.width(220.dp).height(330.dp).align(Alignment.CenterHorizontally).clip(RoundedCornerShape(24.dp)))
            Text(movie.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(movie.year.ifBlank { "Release date TBA" }, style = MaterialTheme.typography.titleMedium)
                SuggestionChip(onClick = {}, label = { Text("★ ${String.format(Locale.US, "%.1f", movie.rating)} / 10") })
            }
            favoriteAction()
            Text("Overview", style = MaterialTheme.typography.titleLarge)
            Text(movie.overview.ifBlank { "No description available yet." }, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Movie data provided by TMDB", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
