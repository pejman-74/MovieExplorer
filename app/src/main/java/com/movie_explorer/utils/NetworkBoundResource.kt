package com.movie_explorer.utils

import com.movie_explorer.wrapper.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
) = channelFlow {
    val data = query().first()

    if (shouldFetch(data)) {
        val loading = launch {
            query().collect { send(Resource.Loading(it)) }
        }
        try {
            val fetched = fetch()
            saveFetchResult(fetched)
            loading.cancel()
            query().collect { send(Resource.Success(it)) }
        } catch (t: Throwable) {
            loading.cancel()
            query().collect { send(Resource.Error(t, it)) }
        }

    } else {
        query().collect { send(Resource.Success(it)) }
    }
}

inline fun <ResultType> coldNetworkBoundResource(
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