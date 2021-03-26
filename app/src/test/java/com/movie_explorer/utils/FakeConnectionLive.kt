package com.movie_explorer.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.movie_explorer.utils.network.ConnectionCheckerInterface


class FakeConnectionLive : ConnectionCheckerInterface {
    private var isNetworkAvailable = true
    fun setIsNetworkAvailable(isNetworkAvailable: Boolean) {
        this.isNetworkAvailable = isNetworkAvailable
    }

    override fun hasInternetConnection(): Boolean {
        return isNetworkAvailable
    }

    override fun hasInternetConnectionLive(): LiveData<Boolean> =
        MutableLiveData(isNetworkAvailable)


}