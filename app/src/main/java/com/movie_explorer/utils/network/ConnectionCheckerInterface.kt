package com.movie_explorer.utils.network

import androidx.lifecycle.LiveData

interface ConnectionCheckerInterface {
    fun hasInternetConnection(): Boolean
    fun hasInternetConnectionLive(): LiveData<Boolean>
}