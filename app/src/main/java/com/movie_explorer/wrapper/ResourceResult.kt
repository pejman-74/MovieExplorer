package com.movie_explorer.wrapper


sealed class ResourceResult<out T> {
    class Success<out T>(val value: T) : ResourceResult<T>()
    class Failure(val isInExceptionMode: Boolean, val message: String? = null) : ResourceResult<Nothing>()
    object Loading : ResourceResult<Nothing>()
}
