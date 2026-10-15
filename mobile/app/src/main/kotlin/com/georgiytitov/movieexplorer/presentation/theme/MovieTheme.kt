package com.georgiytitov.movieexplorer.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CINEMA_COLORS =
    darkColorScheme(
        primary = MovieColors.PRIMARY,
        onPrimary = MovieColors.ON_PRIMARY,
        primaryContainer = MovieColors.PRIMARY_CONTAINER,
        secondary = MovieColors.SECONDARY,
        background = MovieColors.BACKGROUND,
        surface = MovieColors.SURFACE,
        surfaceContainer = MovieColors.SURFACE_CONTAINER,
        surfaceContainerHigh = MovieColors.SURFACE_CONTAINER_HIGH,
        surfaceContainerHighest = MovieColors.SURFACE_CONTAINER_HIGHEST,
    )

@Composable
fun MovieTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CINEMA_COLORS,
        content = content,
    )
}
