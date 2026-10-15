package com.georgiytitov.movieexplorer.presentation.resources

import androidx.annotation.StringRes
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.domain.model.MovieTab
import com.georgiytitov.movieexplorer.domain.model.MovieTab.NOW_PLAYING
import com.georgiytitov.movieexplorer.domain.model.MovieTab.TOP_RATED
import com.georgiytitov.movieexplorer.domain.model.MovieTab.UPCOMING

@get:StringRes
val MovieTab.titleRes: Int
    get() {
        return when (this) {
            NOW_PLAYING -> R.string.now_playing
            UPCOMING -> R.string.upcoming
            TOP_RATED -> R.string.top_rated
        }
    }
