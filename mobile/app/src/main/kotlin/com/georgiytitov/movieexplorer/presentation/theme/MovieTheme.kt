package com.georgiytitov.movieexplorer.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CinemaColors = darkColorScheme(
    primary = Color(0xFFB9CEFF),
    onPrimary = Color(0xFF182D57),
    primaryContainer = Color(0xFF29426E),
    secondary = Color(0xFFC6C5DD),
    background = Color(0xFF101319),
    surface = Color(0xFF101319),
    surfaceContainer = Color(0xFF1C2028),
    surfaceContainerHigh = Color(0xFF262B35),
    surfaceContainerHighest = Color(0xFF313642),
)

@Composable
fun MovieTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CinemaColors, content = content)
}
