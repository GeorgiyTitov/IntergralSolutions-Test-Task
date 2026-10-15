package com.georgiytitov.movieexplorer.data.remote.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class NetworkMonitor(
    context: Context,
) {
    private val manager = context.getSystemService(ConnectivityManager::class.java)
    private val mutableOnline = MutableStateFlow(isOnline())
    val online = mutableOnline.asStateFlow()

    init {
        manager.registerDefaultNetworkCallback(
            NetworkConnectionCallback(onConnectionChanged = ::updateConnectionStatus),
        )
    }

    private fun updateConnectionStatus() {
        mutableOnline.value = isOnline()
    }

    private fun isOnline(): Boolean {
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
    }
}
