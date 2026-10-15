package com.georgiytitov.movieexplorer.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.georgiytitov.movieexplorer.presentation.resources.Dimensions
import com.georgiytitov.movieexplorer.presentation.resources.Strings

@Composable
fun MessageContent(
    title: String,
    message: String,
    retry: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = Dimensions.MESSAGE_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = Dimensions.MEDIUM_SPACING),
        content = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (retry != null) {
                OutlinedButton(
                    onClick = retry,
                    content = {
                        Text(
                            text = Strings.TRY_AGAIN,
                        )
                    },
                )
            }
        },
    )
}
