package com.georgiytitov.movieexplorer.presentation.screens.movies.compose.components.list

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.presentation.components.feedback.MessageContent
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions

@Composable
internal fun MovieListFooter(
    loading: Boolean,
    @StringRes error: Int?,
    refresh: () -> Unit,
) {
    if (loading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = Dimensions.LARGE_SPACING),
            contentAlignment = Alignment.Center,
            content = { CircularProgressIndicator() },
        )
    }
    if (error != null) {
        MessageContent(
            title = stringResource(id = R.string.load_more_error),
            message = stringResource(id = error),
            retry = refresh,
        )
    }
}
