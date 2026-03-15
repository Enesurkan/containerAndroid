package com.example.altintakipandroid.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Ağ bağlantı durumunu izler (iOS NetworkMonitor ile aynı amaç).
 * MainActivity.onCreate içinde NetworkMonitor.init(applicationContext) ile başlatılır.
 */
object NetworkMonitor {
    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private var connectivityManager: ConnectivityManager? = null
    private var callback: ConnectivityManager.NetworkCallback? = null

    fun init(context: Context) {
        if (connectivityManager != null) return
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return
        connectivityManager = cm
        updateState(cm)
        callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isConnected.value = true
            }
            override fun onLost(network: Network) {
                updateState(cm)
            }
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                _isConnected.value = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            }
        }
        cm.registerDefaultNetworkCallback(callback!!)
    }

    private fun updateState(cm: ConnectivityManager) {
        val active = cm.activeNetwork
        val caps = active?.let { cm.getNetworkCapabilities(it) }
        _isConnected.value = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
