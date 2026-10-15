package com.georgiytitov.movieexplorer.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import com.georgiytitov.movieexplorer.presentation.resources.Strings

@Composable
fun FavoriteButton(
    selected: Boolean,
    toggle: () -> Unit,
) {
    IconToggleButton(
        checked = selected,
        onCheckedChange = { toggle() },
        content = {
            Icon(
                imageVector = if (selected) {
                    Icons.Filled.Favorite
                } else {
                    Icons.Outlined.FavoriteBorder
                },
                contentDescription =
                    if (selected) {
                        Strings.REMOVE_FROM_FAVORITES
                    } else {
                        Strings.ADD_TO_FAVORITES
                    },
            )
        },
    )
}
