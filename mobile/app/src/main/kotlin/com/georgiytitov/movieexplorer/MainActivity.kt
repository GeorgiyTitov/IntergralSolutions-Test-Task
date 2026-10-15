package com.georgiytitov.movieexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.georgiytitov.movieexplorer.presentation.MovieApp
import com.georgiytitov.movieexplorer.presentation.theme.MovieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        val graph = (application as MovieApplication).graph
        setContent(
            content = {
                MovieTheme(
                    content = {
                        MovieApp(
                            graph = graph,
                        )
                    },
                )
            },
        )
    }
}
