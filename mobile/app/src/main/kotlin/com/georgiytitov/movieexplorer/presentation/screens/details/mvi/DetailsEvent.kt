package com.georgiytitov.movieexplorer.presentation.screens.details.mvi

sealed interface DetailsEvent {
    data object Refresh : DetailsEvent

    data object Back : DetailsEvent
}
