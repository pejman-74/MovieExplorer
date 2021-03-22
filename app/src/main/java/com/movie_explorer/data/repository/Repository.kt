package com.movie_explorer.data.repository

import com.movie_explorer.data.network.MovieApis
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class Repository @Inject constructor(private val movieApis: MovieApis) : BaseRepository() {

    suspend fun searchMovieApis(query: String) = safeApiCall {
        movieApis.searchMovie(query)
    }
}