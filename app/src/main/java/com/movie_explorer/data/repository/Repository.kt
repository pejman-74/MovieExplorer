package com.movie_explorer.data.repository

import com.movie_explorer.data.database.MovieAndFavoriteMovie
import com.movie_explorer.data.database.dao.FavoriteMovieDao
import com.movie_explorer.data.database.dao.MovieDao
import com.movie_explorer.data.database.dao.MovieDetailDao
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.data.model.Movie
import com.movie_explorer.data.model.MovieDetail
import com.movie_explorer.data.network.MovieApis
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class Repository @Inject constructor(
    private val movieApis: MovieApis,
    private val movieDao: MovieDao,
    private val favoriteMovieDao: FavoriteMovieDao,
    private val movieDetailDao: MovieDetailDao
) : RepositoryInterface, BaseRepository() {

    //Api calls

    override suspend fun searchMovieApi(query: String?) = safeApiCall {
        movieApis.searchMovie(query)
    }

    override suspend fun getMovieDetailApi(movie_id: String) = safeApiCall {
        movieApis.getMovieDetail(movie_id)
    }


    /*Database calls*/

    //movie
    override suspend fun saveMovie(movies: List<Movie>) = movieDao.insertMovie(movies)

    override fun getAllMovies() = movieDao.getAllMovies()

    override suspend fun searchMovieByName(query: String?) = movieDao.searchMovieByName(query)


    //favoriteMovie
    override suspend fun saveFavoriteMovie(favoriteMovie: FavoriteMovie) =
        favoriteMovieDao.insertFavoriteMovie(favoriteMovie)

    override suspend fun deleteFavoriteMovie(movieId: Int) =
        favoriteMovieDao.deleteFavoriteMovie(movieId)

    override fun favoriteMovies(): Flow<List<MovieAndFavoriteMovie>> =
        favoriteMovieDao.getAllMovieAndFavoriteMovie()


    //MovieDetail
    override suspend fun saveMovieDetail(movieDetail: MovieDetail) =
        movieDetailDao.insertMovieDetail(movieDetail)

    override suspend fun getMovieDetail(movieDetailId: Int): MovieDetail? =
        movieDetailDao.getMovieDetail(movieDetailId)

}