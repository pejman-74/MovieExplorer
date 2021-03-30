package com.movie_explorer.utils.network

import androidx.lifecycle.LiveData

interface ConnectionCheckerInterface {
  suspend fun hasInternetConnection(): Boolean
    fun hasInternetConnectionLive(): LiveData<Boolean>
}