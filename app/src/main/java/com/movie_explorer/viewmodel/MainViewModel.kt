package com.movie_explorer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movie_explorer.data.model.MovieApiResponse
import com.movie_explorer.data.repository.Repository
import com.movie_explorer.wrapper.ResourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _movieSearchResponse = MutableLiveData<ResourceResult<MovieApiResponse>>()
    val movieSearchResponse: MutableLiveData<ResourceResult<MovieApiResponse>> get() = _movieSearchResponse
    fun searchMovie(query: String) = viewModelScope.launch {
        _movieSearchResponse.postValue(ResourceResult.Loading)
        _movieSearchResponse.postValue(repository.searchMovieApis(query))
    }

}