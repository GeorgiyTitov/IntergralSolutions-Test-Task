package com.georgiytitov.movieexplorer.data.remote.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

internal class NetworkConnectionCallback(
    private val onConnectionChanged: () -> Unit,
) : ConnectivityManager.NetworkCallback() {
    override fun onAvailable(network: Network) {
        onConnectionChanged()
    }

    override fun onLost(network: Network) {
        onConnectionChanged()
    }

    override fun onCapabilitiesChanged(
        network: Network,
        capabilities: NetworkCapabilities,
    ) {
        onConnectionChanged()
    }
}
