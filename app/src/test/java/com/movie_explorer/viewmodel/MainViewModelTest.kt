package com.movie_explorer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.movie_explorer.MainCoroutineRule
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.data.model.MovieApiResponse
import com.movie_explorer.data.model.MovieDetail
import com.movie_explorer.data.repositroy.FakeRepository
import com.movie_explorer.getOrAwaitValue
import com.movie_explorer.utils.FakeConnectionLive
import com.movie_explorer.utils.dummySuccessApiResponse
import com.movie_explorer.utils.dummySuccessGetMovieDetailApiResponse
import com.movie_explorer.wrapper.ResourceResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineScope = MainCoroutineRule()

    lateinit var fakeRepository: FakeRepository
    lateinit var fakeConnectionChecker: FakeConnectionLive
    lateinit var mainViewModel: MainViewModel


    private val dummyMovieApisResponse: MovieApiResponse =
        Gson().fromJson(dummySuccessApiResponse, MovieApiResponse::class.java)

    private val dummyGetMovieDetailApiResponse: MovieDetail =
        Gson().fromJson(dummySuccessGetMovieDetailApiResponse, MovieDetail::class.java)

    private val favoriteMovie = FavoriteMovie(1)

    @Before
    fun setUp() {
        fakeRepository = FakeRepository()
        fakeConnectionChecker = FakeConnectionLive()
        mainViewModel = MainViewModel(fakeRepository, fakeConnectionChecker)
    }


    @Test
    fun `search movie with available network, should return data from api call`() {
        mainViewModel.searchMovie()
        val movieSearchResponseValue =
            mainViewModel.movieSearchResponse.getOrAwaitValue() as ResourceResult.Success
        assertThat(movieSearchResponseValue.value.movies.first())
            .isEqualTo(dummyMovieApisResponse.movies.first())
    }

    @Test
    fun `search movie with available network, should cache movies in to db`() {
        mainViewModel.searchMovie()
        assertThat(mainViewModel.allCacheMovies.getOrAwaitValue()).isEqualTo(
            dummyMovieApisResponse.movies
        )
    }

    @Test
    fun `search movie with NOT available network, should NOT cache movies in to db`() {
        fakeConnectionChecker.setIsNetworkAvailable(false)
        mainViewModel.searchMovie()
        assertThat(mainViewModel.allCacheMovies.getOrAwaitValue()).isEmpty()
    }


    @Test
    fun `search movie with NOT available network, should return movies from cached`() {

        //for filling database
        mainViewModel.searchMovie()

        //turn off the network and then searching
        fakeConnectionChecker.setIsNetworkAvailable(false)
        mainViewModel.searchMovie()

        val movieSearchResponseValue =
            mainViewModel.movieSearchResponse.getOrAwaitValue() as ResourceResult.Success

        assertThat(movieSearchResponseValue.value.movies).isEqualTo(dummyMovieApisResponse.movies)
    }

    @Test
    fun `insert favorite movie,should return in allMovieAndFavoriteMovie`() {

        //for filling database
        mainViewModel.searchMovie()

        mainViewModel.saveFavoriteMovie(favoriteMovie)

        val movieAndFavoriteMovie = mainViewModel.allMovieAndFavoriteMovie.getOrAwaitValue().first()

        assertThat(movieAndFavoriteMovie.movie).isEqualTo(dummyMovieApisResponse.movies.first())
        assertThat(movieAndFavoriteMovie.favoriteMovie).isEqualTo(favoriteMovie)
    }

    @Test
    fun `delete favorite movie,should not be exist inside allMovieAndFavoriteMovie`() {

        //for filling database
        mainViewModel.searchMovie()

        mainViewModel.saveFavoriteMovie(favoriteMovie)

        val allMovieAndFavoriteMovie = mainViewModel.allMovieAndFavoriteMovie

        val movieAndFavoriteMovie =allMovieAndFavoriteMovie.getOrAwaitValue().first()

        assertThat(movieAndFavoriteMovie.movie).isEqualTo(dummyMovieApisResponse.movies.first())
        assertThat(movieAndFavoriteMovie.favoriteMovie).isEqualTo(favoriteMovie)

        mainViewModel.deleteFavoriteMovie(favoriteMovie.movieId)


        assertThat(allMovieAndFavoriteMovie.getOrAwaitValue()).isEmpty()


    }

    @Test
    fun `get movie detail`() {
        mainViewModel.getMovieDetail("1")
        val movieDetailResponse =
            mainViewModel.movieDetailResponse.getOrAwaitValue() as ResourceResult.Success
        assertThat(movieDetailResponse.value).isEqualTo(dummyGetMovieDetailApiResponse)
    }
}