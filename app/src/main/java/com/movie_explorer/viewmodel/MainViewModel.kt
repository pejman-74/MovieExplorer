package com.movie_explorer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.data.model.MovieDetail
import com.movie_explorer.data.repository.RepositoryInterface
import com.movie_explorer.wrapper.RefreshType
import com.movie_explorer.wrapper.Resource
import com.movie_explorer.wrapper.ResourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: RepositoryInterface,
) : ViewModel() {


    private val refreshTriggerChannel = Channel<RefreshType>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    val feedMovie = refreshTrigger.flatMapLatest { refreshType ->

        when (refreshType) {
            RefreshType.Force -> repository.getReadyMovies(null, true)
            RefreshType.Normal -> repository.getReadyMovies(null, false)
            is RefreshType.Search -> repository.getReadyMovies(refreshType.query, true)
        }

    }.stateIn(viewModelScope, SharingStarted.Lazily, null)


    fun refreshMovieFeed(refreshType: RefreshType) = viewModelScope.launch {
        if (feedMovie.value !is Resource.Loading)
            when (refreshType) {
                RefreshType.Force -> refreshTriggerChannel.send(RefreshType.Force)
                RefreshType.Normal -> refreshTriggerChannel.send(RefreshType.Normal)
                is RefreshType.Search -> refreshTriggerChannel.send(RefreshType.Search(refreshType.query))
            }
    }


    fun saveFavoriteMovie(favoriteMovie: FavoriteMovie) =
        viewModelScope.launch { repository.saveFavoriteMovie(favoriteMovie) }

    fun deleteFavoriteMovie(movieId: Int) =
        viewModelScope.launch { repository.deleteFavoriteMovie(movieId) }

    val allMovieAndFavoriteMovie = repository.favoriteMovies().asLiveData()


    private val _movieDetailResponse = MutableLiveData<ResourceResult<MovieDetail?>>()
    val movieDetailResponse: MutableLiveData<ResourceResult<MovieDetail?>> get() = _movieDetailResponse
    fun getMovieDetail(movie_id: Int) = viewModelScope.launch {
        _movieDetailResponse.postValue(ResourceResult.Loading)
        //TODO :
        if (true) {
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