package com.georgiytitov.movieexplorer

import android.app.Application
import com.georgiytitov.movieexplorer.di.AppGraph

class MovieApplication : Application() {
    val graph: AppGraph by lazy { AppGraph() }
}
