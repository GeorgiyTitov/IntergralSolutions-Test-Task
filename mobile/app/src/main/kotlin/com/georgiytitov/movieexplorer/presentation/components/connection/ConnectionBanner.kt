package com.georgiytitov.movieexplorer.presentation.components.connection

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus.CACHED
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus.CONNECTED
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus.FALLBACK_TMDB
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus.OFFLINE
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions

@Composable
fun ConnectionBanner(connectionStatus: ConnectionStatus) {
    val message = when (connectionStatus) {
        OFFLINE -> stringResource(id = R.string.offline_message)
        FALLBACK_TMDB -> stringResource(id = R.string.direct_tmdb_message)
        CACHED -> stringResource(id = R.string.cached_message)
        CONNECTED -> return
    }
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth(),
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
