package com.georgiytitov.movieexplorer.presentation.components.favorites

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.georgiytitov.movieexplorer.R

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
                contentDescription = if (selected) {
                    stringResource(id = R.string.remove_from_favorites)
                } else {
                    stringResource(id = R.string.add_to_favorites)
                },
            )
        },
    )
}
