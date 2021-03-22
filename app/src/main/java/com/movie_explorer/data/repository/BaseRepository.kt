package com.movie_explorer.data.repository

import com.movie_explorer.BuildConfig
import com.movie_explorer.wrapper.ResourceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseRepository {
    suspend fun <T> safeApiCall(apiCall: suspend () -> T): ResourceResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                ResourceResult.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                if (BuildConfig.DEBUG)
                    throwable.printStackTrace()
                ResourceResult.Failure(throwable.message)
            }
        }
    }
}