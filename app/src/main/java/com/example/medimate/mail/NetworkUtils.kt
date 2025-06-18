package com.example.medimate.mail


import android.content.Context
import android.net.ConnectivityManager

object NetworkUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected == true
    }
}