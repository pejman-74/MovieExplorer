package com.movie_explorer.utils.interceptor

import com.movie_explorer.utils.network.ConnectionObserver
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ConnectionInterceptor(private val connectionObserver: ConnectionObserver) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            if (connectionObserver.hasInternetConnection())
                return@runBlocking chain.proceed(chain.request())
            else
                throw IOException("No internet connection")
        }
    }
}