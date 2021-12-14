package com.example.et_practz

import android.util.Log
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket



object DoesNetworkHaveInternet {
    private const val TAG = "NET-CHECK"
    // This function tries to connect to Google server if it fails we know that there is no Internet
    fun execute(): Boolean {
        return try{
            Log.d(TAG, "PING GOOGLE")
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            Log.d(TAG, "Ping success.")
            true
        }catch(e: IOException){
            Log.d(TAG, "No internet connection. $e")
            false
        }
    }

}