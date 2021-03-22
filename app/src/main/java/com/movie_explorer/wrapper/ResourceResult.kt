package com.movie_explorer.wrapper


sealed class ResourceResult<out T> {
    class Success<out T>(val value: T) : ResourceResult<T>()
    class Failure(val message: String?) : ResourceResult<Nothing>()
    object Loading : ResourceResult<Nothing>()
}
