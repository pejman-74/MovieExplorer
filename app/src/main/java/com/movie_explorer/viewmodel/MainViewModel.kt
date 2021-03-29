package com.movie_explorer.viewmodel

import androidx.lifecycle.*
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.data.model.MovieApiResponse
import com.movie_explorer.data.model.MovieDetail
import com.movie_explorer.data.repository.RepositoryInterface
import com.movie_explorer.utils.network.ConnectionCheckerInterface
import com.movie_explorer.wrapper.ResourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
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

            cacheMovieApiResponseResourceResult(movieApiResponse)
        } else {

            val movies = repository.searchMovieByName(query)
            //convert to MovieApiResponse
            val movieApiResponse = MovieApiResponse(movies)
            _movieSearchResponse.postValue(ResourceResult.Success(movieApiResponse))

        }
    }

    private fun cacheMovieApiResponseResourceResult(movieApiResponse: ResourceResult<MovieApiResponse>) =
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


    fun saveFavoriteMovie(favoriteMovie: FavoriteMovie) =
        viewModelScope.launch { repository.saveFavoriteMovie(favoriteMovie) }

    fun deleteFavoriteMovie(movieId: Int) =
        viewModelScope.launch { repository.deleteFavoriteMovie(movieId) }

    val allMovieAndFavoriteMovie = repository.favoriteMovies().asLiveData()


    private val _movieDetailResponse = MutableLiveData<ResourceResult<MovieDetail?>>()
    val movieDetailResponse: MutableLiveData<ResourceResult<MovieDetail?>> get() = _movieDetailResponse
    fun getMovieDetail(movie_id: Int) = viewModelScope.launch {
        _movieDetailResponse.postValue(ResourceResult.Loading)

        if (connectionChecker.hasInternetConnection()) {
            val movieDetailApiResponse = repository.getMovieDetailApi(movie_id.toString())
            _movieDetailResponse.postValue(movieDetailApiResponse)
            cacheMovieDetailResourceResult(movieDetailApiResponse)

        } else {
            val movieDetail = repository.getMovieDetail(movie_id)
            //convert to MovieApiResponse
            val movieDetailApiResponse = ResourceResult.Success(movieDetail)
            _movieDetailResponse.postValue(movieDetailApiResponse)
        }
    }


    private fun cacheMovieDetailResourceResult(movieDetailApiResponse: ResourceResult<MovieDetail>) =
        viewModelScope.launch {
            when (movieDetailApiResponse) {
                is ResourceResult.Success -> repository.saveMovieDetail(movieDetailApiResponse.value)
                else -> Unit
            }
        }
}