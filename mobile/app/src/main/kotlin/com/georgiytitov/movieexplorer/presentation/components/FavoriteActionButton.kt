package com.georgiytitov.movieexplorer.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions
import com.georgiytitov.movieexplorer.presentation.resources.Strings

@Composable
fun FavoriteActionButton(
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        content = {
            Icon(
                imageVector = if (selected) {
                    Icons.Filled.Favorite
                } else {
                    Icons.Outlined.FavoriteBorder
                },
                contentDescription = null,
            )
            Spacer(
                modifier = Modifier
                    .width(width = Dimensions.SMALL_SPACING),
            )
            Text(
                text = if (selected) Strings.REMOVE_FROM_FAVORITES else Strings.SAVE_TO_FAVORITES,
            )
        },
    )
}
