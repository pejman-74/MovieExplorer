package com.movie_explorer.data.repository

import com.movie_explorer.data.database.MovieAndFavoriteMovie
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.data.model.Movie
import com.movie_explorer.data.model.MovieApiResponse
import com.movie_explorer.data.model.MovieDetail
import com.movie_explorer.wrapper.ResourceResult
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {

    /*Api calls*/
    suspend fun searchMovieApi(query: String? = null): ResourceResult<MovieApiResponse>

    suspend fun getMovieDetailApi(movie_id: String): ResourceResult<MovieDetail>

    /*Database calls*/

    //movie
    suspend fun saveMovie(movies: List<Movie>)

    fun getAllMovies(): Flow<List<Movie>>

    suspend fun searchMovieByName(query: String): List<Movie>


    //favoriteMovie
    suspend fun saveFavoriteMovie(favoriteMovie: FavoriteMovie)

    suspend fun deleteFavoriteMovie(movieId: Int)

    fun favoriteMovies(): Flow<List<MovieAndFavoriteMovie>>


    //MovieDetail
    suspend fun saveMovieDetail(movieDetail: MovieDetail)

    suspend fun getMovieDetail(movieDetailId: Int): MovieDetail?
}