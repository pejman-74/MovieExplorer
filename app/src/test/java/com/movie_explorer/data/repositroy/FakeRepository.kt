package com.movie_explorer.data.repositroy

import com.google.gson.Gson
import com.movie_explorer.data.database.MovieAndFavoriteMovie
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.data.model.Movie
import com.movie_explorer.data.model.MovieApiResponse
import com.movie_explorer.data.model.MovieDetail
import com.movie_explorer.data.repository.RepositoryInterface
import com.movie_explorer.utils.dummySuccessApiResponse
import com.movie_explorer.utils.dummySuccessGetMovieDetailApiResponse
import com.movie_explorer.wrapper.ResourceResult
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow

class FakeRepository : RepositoryInterface {
    private val dummyMovieApisResponse: MovieApiResponse =
        Gson().fromJson(dummySuccessApiResponse, MovieApiResponse::class.java)

    private val dummyGetMovieDetailApiResponse: MovieDetail =
        Gson().fromJson(dummySuccessGetMovieDetailApiResponse, MovieDetail::class.java)

    private val fakeMovieDataBase = ArrayList<Movie>()
    private val fakeFavoriteMovieDataBase = ArrayList<FavoriteMovie>()
    private val fakeMovieDetailApiDataBase = ArrayList<MovieDetail>()

    override suspend fun searchMovieApi(query: String?): ResourceResult<MovieApiResponse> {
        return ResourceResult.Success(dummyMovieApisResponse)
    }

    override suspend fun getMovieDetailApi(movie_id: String): ResourceResult<MovieDetail> {
        return ResourceResult.Success(dummyGetMovieDetailApiResponse)
    }


    override suspend fun saveMovie(movies: List<Movie>) {
        fakeMovieDataBase.addAll(movies)
    }

    override fun getAllMovies(): Flow<List<Movie>> {
        return flow {
            emit(fakeMovieDataBase.toList())
        }
    }

    override suspend fun searchMovieByName(query: String?): List<Movie> {
        return if (query == null)
            fakeMovieDataBase
        else
            fakeMovieDataBase.filter { it.title.contains(query) }
    }

    override suspend fun saveFavoriteMovie(favoriteMovie: FavoriteMovie) {
        fakeFavoriteMovieDataBase.add(favoriteMovie)
    }

    override suspend fun deleteFavoriteMovie(movieId: Int) {
        fakeFavoriteMovieDataBase.dropWhile { it.movieId == movieId }
    }

    @FlowPreview
    override fun favoriteMovies(): Flow<List<MovieAndFavoriteMovie>> =
        fakeMovieDataBase.asFlow().flatMapConcat { movie ->
            val movieAndFavoriteMovie = ArrayList<MovieAndFavoriteMovie>()
            fakeFavoriteMovieDataBase.forEach { favoriteMovie ->
                if (movie.id == favoriteMovie.movieId)
                    movieAndFavoriteMovie.add(MovieAndFavoriteMovie(movie, favoriteMovie))
            }
            flow {
                emit(movieAndFavoriteMovie.toList())
            }

        }

    override suspend fun saveMovieDetail(movieDetail: MovieDetail) {
        fakeMovieDetailApiDataBase.add(movieDetail)
    }

    override suspend fun getMovieDetail(movieDetailId: Int): MovieDetail? {
        return fakeMovieDetailApiDataBase.find { it.id == movieDetailId }
    }

}