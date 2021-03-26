package com.movie_explorer.data.repository

import com.movie_explorer.data.database.dao.MovieDao
import com.movie_explorer.data.model.Movie
import com.movie_explorer.data.network.MovieApis
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class Repository @Inject constructor(
    private val movieApis: MovieApis,
    private val movieDao: MovieDao
) : RepositoryInterface, BaseRepository() {

    //Api calls

    override suspend fun searchMovieApi(query: String?) = safeApiCall {
        movieApis.searchMovie(query)
    }


    //Database calls

    override suspend fun saveMovie(movies: List<Movie>) = movieDao.insertMovie(movies)

    override fun getAllMovies() = movieDao.getAllMovies()

    override suspend fun searchMovieByName(query: String?) = movieDao.searchMovieByName(query)
}