package com.georgiytitov.movieexplorer.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.georgiytitov.movieexplorer.domain.model.DataOrigin
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions
import com.georgiytitov.movieexplorer.presentation.resources.Strings

@Composable
fun ConnectionBanner(
    online: Boolean,
    origin: DataOrigin,
) {
    val message =
        when {
            !online -> Strings.OFFLINE_MESSAGE
            origin == DataOrigin.TMDB -> Strings.DIRECT_TMDB_MESSAGE
            origin == DataOrigin.CACHE -> Strings.CACHED_MESSAGE
            else -> return
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
