package com.movie_explorer.data.repositroy

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.movie_explorer.data.model.Movie
import com.movie_explorer.data.model.MovieApiResponse
import com.movie_explorer.data.repository.RepositoryInterface
import com.movie_explorer.utils.dummySuccessApiResponse
import com.movie_explorer.wrapper.ResourceResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository : RepositoryInterface {
    private val dummyMovieApisResponse: MovieApiResponse =
        Gson().fromJson(dummySuccessApiResponse, MovieApiResponse::class.java)

    private val fakeMovieDataBase = MutableLiveData<List<Movie>>(emptyList())

    override suspend fun searchMovieApi(query: String?): ResourceResult<MovieApiResponse> {
        return ResourceResult.Success(dummyMovieApisResponse)
    }


    override suspend fun saveMovie(movies: List<Movie>) {
        fakeMovieDataBase.value = fakeMovieDataBase.value?.plus(movies)
    }

    override fun getAllMovies(): Flow<List<Movie>> {
        return flow {
            emit(fakeMovieDataBase.value!!)
        }
    }

    override suspend fun searchMovieByName(query: String?): List<Movie> {
        return if (query == null)
            fakeMovieDataBase.value!!
        else
            fakeMovieDataBase.value!!.filter { it.title.contains(query) }
    }
}