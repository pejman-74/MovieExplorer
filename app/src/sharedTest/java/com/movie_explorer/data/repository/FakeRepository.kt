package com.movie_explorer.data.repository

import com.google.gson.Gson
import com.movie_explorer.data.database.MovieAndFavoriteMovie
import com.movie_explorer.data.model.*
import com.movie_explorer.utils.dummySuccessApiResponse
import com.movie_explorer.utils.dummySuccessGetMovieDetailApiResponse
import com.movie_explorer.utils.interceptor.NoInternetException
import com.movie_explorer.utils.networkBoundResource
import com.movie_explorer.utils.wrapEspressoIdlingResource
import com.movie_explorer.wrapper.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

enum class InternetStatus { ON, OFF }

class FakeRepository  : RepositoryInterface {
    val dummyMovieApisResponse: MovieApiResponse =
        Gson().fromJson(dummySuccessApiResponse, MovieApiResponse::class.java)

    val dummyGetMovieDetailApiResponse: MovieDetail =
        Gson().fromJson(dummySuccessGetMovieDetailApiResponse, MovieDetail::class.java)

    private val movieDataBase = MutableStateFlow<List<Movie>>(emptyList())
    private val favoriteMovieDataBase = MutableStateFlow<List<FavoriteMovie>>(emptyList())
    private val movieDetailApiDataBase = MutableStateFlow<List<MovieDetail>>(emptyList())

    private val relatedMovieAndFavorite = MutableStateFlow<List<MovieAndFavoriteMovie>>(emptyList())

    private var internetStatus = InternetStatus.ON

    private var latancy = 0L

    fun setInternetStatus(internetStatus: InternetStatus) {
        this.internetStatus = internetStatus
    }

    fun setInternetLatency(time: Long) {
        latancy = time
    }

    override suspend fun getReadyMovies(query: String?, forceRefresh: Boolean):
            Flow<Resource<List<Movie>>> = networkBoundResource(
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

    override suspend fun getReadyMovieDetail(movie_id: String): Flow<Resource<MovieDetail>> =
        networkBoundResource(
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

    override suspend fun searchMovieApi(query: String?): MovieApiResponse {
        return when {
            internetStatus == InternetStatus.OFF -> throw NoInternetException()
            query.isNullOrEmpty() -> wrapEspressoIdlingResource {
                delay(latancy)
                dummyMovieApisResponse
            }
            else -> wrapEspressoIdlingResource {
                delay(latancy)
                val matchMovies =
                    dummyMovieApisResponse.movies.filter { it.title.contains(query) }
                val metadata = Metadata("1", 1, 10, matchMovies.size)
                MovieApiResponse(matchMovies, metadata)
            }

        }
    }

    override suspend fun getMovieDetailApi(movie_id: String): MovieDetail =

        when (internetStatus) {
            InternetStatus.OFF -> throw NoInternetException()
            else -> wrapEspressoIdlingResource {
                delay(latancy)
                dummyGetMovieDetailApiResponse
            }
        }


    override suspend fun saveMovie(movies: List<Movie>) {
        movieDataBase.emit(movieDataBase.value.plus(movies))
    }

    override suspend fun getAllMovies(): List<Movie> {
        return movieDataBase.value
    }


    override suspend fun searchMovieByName(query: String): List<Movie> {
        return if (query == "")
            movieDataBase.value
        else
            movieDataBase.value.filter { it.title.contains(query) }
    }

    override suspend fun saveFavoriteMovie(favoriteMovie: FavoriteMovie) {
        favoriteMovieDataBase.emit(favoriteMovieDataBase.value.plus(favoriteMovie))

        val movie = movieDataBase.value.find { it.id == favoriteMovie.movieId }
        if (movie != null)
            relatedMovieAndFavorite.emit(
                relatedMovieAndFavorite.value.plus(MovieAndFavoriteMovie(movie, favoriteMovie))
            )
    }

    override suspend fun deleteFavoriteMovie(movieId: Int) {
        favoriteMovieDataBase.emit(
            favoriteMovieDataBase.value.dropWhile { it.movieId == movieId })

        val movie = movieDataBase.value.find { it.id == movieId }
        if (movie != null)
            relatedMovieAndFavorite.emit(
                relatedMovieAndFavorite.value.dropWhile { it.favoriteMovie.movieId == movieId }
            )
    }

    override fun favoriteMovies(): Flow<List<MovieAndFavoriteMovie>> {
        return relatedMovieAndFavorite
    }

    override suspend fun saveMovieDetail(movieDetail: MovieDetail) {
        movieDetailApiDataBase.emit(movieDetailApiDataBase.value.plus(movieDetail))
    }

    override suspend fun getMovieDetail(movieDetailId: String): MovieDetail? {
        return movieDetailApiDataBase.value.find { it.id.toString() == movieDetailId }
    }


}