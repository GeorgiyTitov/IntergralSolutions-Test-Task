package com.georgiytitov.movieexplorer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.georgiytitov.movieexplorer.presentation.MovieApp
import com.georgiytitov.movieexplorer.presentation.theme.MovieTheme

class MainActivity : ComponentActivity() {
    private val localNetworkPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { showApp() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= LOCAL_NETWORK_PERMISSION_MIN_SDK &&
            checkSelfPermission(Manifest.permission.ACCESS_LOCAL_NETWORK) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            localNetworkPermission.launch(Manifest.permission.ACCESS_LOCAL_NETWORK)
        } else {
            showApp()
        }
    }

    private fun showApp() {
        val graph = (application as MovieApplication).graph
        setContent(content = { MovieTheme(content = { MovieApp(graph = graph) }) })
    }

    private companion object {
        const val LOCAL_NETWORK_PERMISSION_MIN_SDK = 37
    }
}
