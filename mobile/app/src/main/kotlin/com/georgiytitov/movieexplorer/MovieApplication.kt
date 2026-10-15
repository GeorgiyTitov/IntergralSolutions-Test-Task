package com.georgiytitov.movieexplorer

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.georgiytitov.movieexplorer.di.AppGraph

class MovieApplication :
    Application(),
    SingletonImageLoader.Factory {
    internal val graph: AppGraph by lazy { AppGraph(this) }

    override fun newImageLoader(context: Context): ImageLoader = graph.imageLoader
}
