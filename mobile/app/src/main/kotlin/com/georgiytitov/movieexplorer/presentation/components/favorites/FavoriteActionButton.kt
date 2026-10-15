package com.georgiytitov.movieexplorer.presentation.components.favorites

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions

@Composable
fun FavoriteActionButton(
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        content = {
            Icon(
                imageVector = if (selected) {
                    Icons.Filled.Favorite
                } else {
                    Icons.Outlined.FavoriteBorder
                },
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(width = Dimensions.SMALL_SPACING))
            Text(
                text = if (selected) {
                    stringResource(id = R.string.remove_from_favorites)
                } else {
                    stringResource(id = R.string.save_to_favorites)
                },
            )
        },
    )
}
