package com.georgiytitov.movieexplorer.presentation.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal inline fun <State> MutableStateFlow<State>.updateState(transform: State.() -> State) {
    update(transform)
}
