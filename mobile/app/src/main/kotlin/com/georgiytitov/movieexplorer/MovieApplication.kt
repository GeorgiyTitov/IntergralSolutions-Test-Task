package com.georgiytitov.movieexplorer

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.georgiytitov.movieexplorer.data.remote.posterImageLoader
import com.georgiytitov.movieexplorer.di.AppGraph

class MovieApplication :
    Application(),
    SingletonImageLoader.Factory {
    val graph: AppGraph by lazy { AppGraph(this) }

    override fun newImageLoader(context: Context): ImageLoader =
        posterImageLoader(context, graph.network)
}
