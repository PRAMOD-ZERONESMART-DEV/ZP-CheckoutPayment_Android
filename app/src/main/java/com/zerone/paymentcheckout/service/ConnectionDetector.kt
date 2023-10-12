package com.zerone.paymentcheckout.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionDetector {

    private val _context: Context? = null

    private fun isConnectingToInternet(): Boolean {
        val connectivity = _context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null) for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) {
                return true
            }
        }
        return false
    }

    private fun isConnectingToWifi(): Boolean {
        val connManager = _context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mWifi = connManager
            .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (mWifi != null) {
            if (mWifi.state == NetworkInfo.State.CONNECTED) return true
        }
        return false
    }
}