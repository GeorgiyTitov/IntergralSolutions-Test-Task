package com.georgiytitov.movieexplorer.presentation.resources

import com.georgiytitov.movieexplorer.domain.model.MovieTab

val MovieTab.title: String
    get() {
        return when (this) {
            MovieTab.NOW_PLAYING -> Strings.NOW_PLAYING
            MovieTab.UPCOMING -> Strings.UPCOMING
            MovieTab.TOP_RATED -> Strings.TOP_RATED
        }
    }
