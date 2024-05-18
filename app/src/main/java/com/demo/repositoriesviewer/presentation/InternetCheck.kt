package com.demo.repositoriesviewer.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress

object InternetCheck {
    private const val AVAILABLE_ADDRESS = "api.github.com"
    private const val VALUE_IS_EMPTY = ""

    suspend fun isInternetAvailable(): Boolean {
        return try {
            val ipAddress: InetAddress = withContext(Dispatchers.IO) {
                InetAddress.getByName(AVAILABLE_ADDRESS)
            }
            !ipAddress.equals(VALUE_IS_EMPTY)
        } catch (e: Exception) {
            false
        }
    }
}