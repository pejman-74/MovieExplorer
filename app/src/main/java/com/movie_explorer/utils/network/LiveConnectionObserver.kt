package com.movie_explorer.utils.network

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory


class LiveConnectionObserver(context: Context) : LiveData<Boolean>(), ConnectionCheckerInterface {


    private var networkCallback: ConnectivityManager.NetworkCallback
    private val cManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()

    init {
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()

        cManager.registerNetworkCallback(networkRequest, networkCallback)

        CoroutineScope(Dispatchers.Main).launch {
            val hasInternet = oneTimeInternetConnectionChecker()
            if (!hasInternet)
                postValue(false)
        }
    }

    fun stopObserving() {
        cManager.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

        /*
          Called when a network is detected. If that network has internet, save it in the Set.
          Source: https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onAvailable(android.net.Network)
         */
        override fun onAvailable(network: Network) {
            CoroutineScope(Dispatchers.Main).launch {
                val hasInternet = checkNetworkInternetConnection(network)
                if (hasInternet) {
                    validNetworks.add(network)
                    checkValidNetworks()
                }
            }
        }

        /*
          If the callback was registered with registerNetworkCallback() it will be called for each network which no longer satisfies the criteria of the callback.
          Source: https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onLost(android.net.Network)
         */
        override fun onLost(network: Network) {
            validNetworks.remove(network)
            checkValidNetworks()
        }

    }

    @Suppress("DEPRECATION")
    private suspend fun oneTimeInternetConnectionChecker(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val activeNetwork = cManager.activeNetwork ?: return false
            checkNetworkInternetConnection(activeNetwork)
        } else {
            val networkInfo = cManager.activeNetworkInfo ?: return false
            if (networkInfo.isConnectedOrConnecting)
                DoesNetworkHaveInternet.execute(SocketFactory.getDefault())
            else
                false
        }

    }

    private suspend fun checkNetworkInternetConnection(network: Network): Boolean {
        val networkCapabilities =
            cManager.getNetworkCapabilities(network) ?: return false

        val hasInternetCapability =
            networkCapabilities.hasCapability(NET_CAPABILITY_INTERNET)

        return if (hasInternetCapability) {
            // check if this network actually has internet
            DoesNetworkHaveInternet.execute(network.socketFactory)
        } else
            false
    }

    /**
     * Send a ping to googles primary DNS.
     * If successful, that means we have internet.
     */
    object DoesNetworkHaveInternet {
        // Make sure to execute this on a background thread.
        suspend fun execute(socketFactory: SocketFactory): Boolean = withContext(Dispatchers.IO) {
            runCatching {
                try {
                    val socket =
                        socketFactory.createSocket() ?: throw IOException("Socket is null.")
                    socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                    socket.close()
                    true
                } catch (e: IOException) {
                    false
                }
            }.isSuccess
        }
    }


    private fun checkValidNetworks() {
        postValue(validNetworks.size > 0)
    }

    override fun getValue(): Boolean = validNetworks.size > 0


    override suspend fun hasInternetConnection() = oneTimeInternetConnectionChecker()


    override fun hasInternetConnectionLive(): LiveData<Boolean> {
        return this
    }
}