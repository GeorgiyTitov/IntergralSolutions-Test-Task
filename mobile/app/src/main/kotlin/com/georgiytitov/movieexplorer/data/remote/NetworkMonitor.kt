package com.georgiytitov.movieexplorer.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkMonitor(
    context: Context,
) {
    private val manager = context.getSystemService(ConnectivityManager::class.java)
    private val mutableOnline = MutableStateFlow(isOnline())
    val online = mutableOnline.asStateFlow()

    init {
        manager.registerDefaultNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    mutableOnline.value = isOnline()
                }

                override fun onLost(network: Network) {
                    mutableOnline.value = isOnline()
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    capabilities: NetworkCapabilities,
                ) {
                    mutableOnline.value = isOnline()
                }
            },
        )
    }

    private fun isOnline(): Boolean {
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
    }
}
