package com.georgiytitov.movieexplorer.presentation.details

sealed interface DetailsEvent {
    data object Refresh : DetailsEvent

    data object Back : DetailsEvent
}
