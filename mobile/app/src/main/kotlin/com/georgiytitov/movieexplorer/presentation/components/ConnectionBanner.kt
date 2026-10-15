package com.georgiytitov.movieexplorer.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions
import com.georgiytitov.movieexplorer.presentation.resources.Strings

@Composable
fun ConnectionBanner(connectionStatus: ConnectionStatus) {
    val message = when (connectionStatus) {
        ConnectionStatus.OFFLINE -> Strings.OFFLINE_MESSAGE
        ConnectionStatus.FALLBACK -> Strings.DIRECT_TMDB_MESSAGE
        ConnectionStatus.CACHED -> Strings.CACHED_MESSAGE
        ConnectionStatus.CONNECTED -> return
    }
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier
            .fillMaxWidth(),
        content = {
            Text(
                text = message,
                modifier = Modifier
                    .padding(
                        horizontal = Dimensions.LARGE_SPACING,
                        vertical = Dimensions.BANNER_VERTICAL_PADDING,
                    ),
                style = MaterialTheme.typography.labelMedium,
            )
        },
    )
}
