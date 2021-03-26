package com.movie_explorer.viewmodel

import androidx.lifecycle.*
import com.movie_explorer.data.model.MovieApiResponse
import com.movie_explorer.data.repository.RepositoryInterface
import com.movie_explorer.utils.network.ConnectionCheckerInterface
import com.movie_explorer.wrapper.ResourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: RepositoryInterface,
    private val connectionChecker: ConnectionCheckerInterface
) : ViewModel() {

    private val _movieSearchResponse = MutableLiveData<ResourceResult<MovieApiResponse>>()
    val movieSearchResponse: MutableLiveData<ResourceResult<MovieApiResponse>> get() = _movieSearchResponse
    fun searchMovie(query: String? = null) = viewModelScope.launch {
        _movieSearchResponse.postValue(ResourceResult.Loading)

        //if connection available,search in remote api and cache the result otherwise search in database
        if (connectionChecker.hasInternetConnection()) {
            val movieApiResponse = repository.searchMovieApi(query)
            _movieSearchResponse.postValue(movieApiResponse)

            cacheResourceResult(movieApiResponse)
        } else {

            val movies = repository.searchMovieByName(query)
            //convert to MovieApiResponse
            val movieApiResponse = MovieApiResponse(movies)
            _movieSearchResponse.postValue(ResourceResult.Success(movieApiResponse))

        }
    }

    private fun cacheResourceResult(movieApiResponse: ResourceResult<MovieApiResponse>) =
        viewModelScope.launch {
            when (movieApiResponse) {
                is ResourceResult.Success -> repository.saveMovie(movieApiResponse.value.movies)
                else -> Unit
            }
        }

    val allCacheMovies = repository.getAllMovies().asLiveData(viewModelScope.coroutineContext)


    fun hasInternetConnection(): Boolean = connectionChecker.hasInternetConnection()
    val hasInternetConnectionLive: LiveData<Boolean> =
        connectionChecker.hasInternetConnectionLive()
}