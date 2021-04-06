package com.movie_explorer.data.repository

import com.movie_explorer.data.database.MovieAndFavoriteMovie
import com.movie_explorer.data.database.dao.FavoriteMovieDao
import com.movie_explorer.data.database.dao.MovieDao
import com.movie_explorer.data.database.dao.MovieDetailDao
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.data.model.Movie
import com.movie_explorer.data.model.MovieApiResponse
import com.movie_explorer.data.model.MovieDetail
import com.movie_explorer.data.network.MovieApis
import com.movie_explorer.utils.networkBoundResource
import com.movie_explorer.wrapper.Resource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
@ExperimentalCoroutinesApi
class Repository @Inject constructor(
    private val movieApis: MovieApis,
    private val movieDao: MovieDao,
    private val favoriteMovieDao: FavoriteMovieDao,
    private val movieDetailDao: MovieDetailDao
) : RepositoryInterface, BaseRepository() {


    override suspend fun getReadyMovies(
        query: String?,
        forceRefresh: Boolean
    ): Flow<Resource<List<Movie>>> = networkBoundResource(
        query = {
            if (query.isNullOrEmpty())
                getAllMovies()
            else
                searchMovieByName(query)
        },
        fetch = { searchMovieApi(query).movies },
        saveFetchResult = {
            saveMovie(it)
        },
        shouldFetch = {
            forceRefresh
        }
    )

    override suspend fun getReadyMovieDetail(movie_id: String):
            Flow<Resource<MovieDetail>> = networkBoundResource(
        query = {
            getMovieDetail(movie_id)
        },
        fetch = { getMovieDetailApi(movie_id) },
        saveFetchResult = {
            saveMovieDetail(it)
        },
        shouldFetch = {
            it == null
        }
    )

    //Api calls

    override suspend fun searchMovieApi(query: String?): MovieApiResponse =
        movieApis.searchMovie(query)


    override suspend fun getMovieDetailApi(movie_id: String): MovieDetail =
        movieApis.getMovieDetail(movie_id)



    /*Database calls*/

    //movie
    override suspend fun saveMovie(movies: List<Movie>) = movieDao.insertMovie(movies)

    override suspend fun getAllMovies() = movieDao.getAllMovies()

    override suspend fun searchMovieByName(query: String) = movieDao.searchMovieByName(query)


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

    override suspend fun getMovieDetail(movieDetailId: String): MovieDetail =
        movieDetailDao.getMovieDetail(movieDetailId)

}