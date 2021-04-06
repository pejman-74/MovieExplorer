package com.movie_explorer.utils

import com.movie_explorer.wrapper.Resource
import kotlinx.coroutines.flow.flow

inline fun <ResultType> networkBoundResource(
    crossinline query: suspend () -> ResultType?,
    crossinline fetch: suspend () -> ResultType,
    crossinline saveFetchResult: suspend (ResultType) -> Unit,
    crossinline shouldFetch: (ResultType?) -> Boolean = { true },
) = flow<Resource<ResultType>> {
    val data = query()

    if (shouldFetch(data)) {

        emit(Resource.Loading(data))

        try {
            val fetched = fetch()
            emit(Resource.Success(fetched))
            saveFetchResult(fetched)
        } catch (t: Throwable) {
            emit(Resource.Error(t, data))
        }

    } else {
        if (data == null)
            emit(Resource.Error(NoSuchElementException("Cant't load data")))
        else
            emit(Resource.Success(data))
    }
}