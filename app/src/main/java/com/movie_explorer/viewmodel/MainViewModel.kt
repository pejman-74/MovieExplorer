package com.movie_explorer.viewmodel

import androidx.lifecycle.*
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.data.model.Movie
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

    fun searchMovie(query: String = "") = viewModelScope.launch {
        _movieSearchResponse.postValue(ResourceResult.Loading)

        val searchMovieResult = ArrayList<Movie>()

        //get from db and post value
        val dbMovies = repository.searchMovieByName(query)
        if (dbMovies.isNotEmpty()) {
            searchMovieResult.addAll(dbMovies)
            //convert to MovieApiResponse
            val convertedMovieApiResponse = MovieApiResponse(searchMovieResult.distinct())
            _movieSearchResponse.postValue(ResourceResult.Success(convertedMovieApiResponse))
        }

        //get from server(if has internet) and merge with db search result and again post value
        if (connectionChecker.hasInternetConnection()) {
            when (val resourceResult = repository.searchMovieApi(query)) {
                is ResourceResult.Success -> {
                    val value = resourceResult.value
                    searchMovieResult.addAll(value.movies)
                    saveMovies(value.movies)
                    val movieApiResponse =
                        MovieApiResponse(searchMovieResult.distinct(), value.metadata)
                    _movieSearchResponse.postValue(ResourceResult.Success(movieApiResponse))
                }
                else -> _movieSearchResponse.postValue(resourceResult)
            }

        } else {
            /*  If the Internet was down and the database search result is empty
              then post failure with true exception mode*/
            _movieSearchResponse.postValue(ResourceResult.Failure(searchMovieResult.isEmpty()))
        }

    }

    private fun saveMovies(movies: List<Movie>) = viewModelScope.launch {
        repository.saveMovie(movies)
    }

    val allCacheMovies = repository.getAllMovies().asLiveData()


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