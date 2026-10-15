package com.georgiytitov.movieexplorer.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.georgiytitov.movieexplorer.presentation.resources.Motion

internal object MovieTransitions {
    val popEnter: EnterTransition = fadeIn(
        animationSpec = tween(durationMillis = Motion.ENTER_DURATION_MILLIS),
    )
    val exit: ExitTransition = fadeOut(
        animationSpec = tween(durationMillis = Motion.EXIT_DURATION_MILLIS),
    )
    val enter: EnterTransition = slideInHorizontally(
        animationSpec = tween(durationMillis = Motion.ENTER_DURATION_MILLIS),
        initialOffsetX = { it / Motion.HORIZONTAL_OFFSET_DIVISOR },
    ) + popEnter
    val popExit: ExitTransition = slideOutHorizontally(
        animationSpec = tween(durationMillis = Motion.ENTER_DURATION_MILLIS),
        targetOffsetX = { it / Motion.HORIZONTAL_OFFSET_DIVISOR },
    ) + exit
}
