package com.movie_explorer.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.movie_explorer.MainCoroutineRule
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.data.model.MovieApiResponse
import com.movie_explorer.data.model.MovieDetail
import com.movie_explorer.data.repository.FakeRepository
import com.movie_explorer.data.repository.InternetStatus
import com.movie_explorer.wrapper.RefreshType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
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
    lateinit var mainViewModel: MainViewModel


    private val dummyMovieApisResponse: MovieApiResponse =
        fakeRepository.dummyMovieApisResponse

    private val dummyGetMovieDetailApiResponse: MovieDetail =
        fakeRepository.dummyGetMovieDetailApiResponse

    private val favoriteMovie = FavoriteMovie(1, "")

    @Before
    fun setUp() {
        fakeRepository = FakeRepository()
        mainViewModel = MainViewModel(fakeRepository)
    }

    /*for filling movie cache*/
    private suspend fun manualFilingMovieCache() {
        fakeRepository.saveMovie(fakeRepository.searchMovieApi().movies)
    }

    /**
     * FEED MOVIE TESTS.
     * */
    @Test
    fun `force movie feed refresh, should return data`() = runBlockingTest {
        mainViewModel.refreshMovieFeed(RefreshType.Force)
        assertThat(mainViewModel.feedMovie.value?.data).isEqualTo(dummyMovieApisResponse.movies)
    }

    @Test
    fun `force movie feed refresh without internet,should return error`() = runBlockingTest {
        fakeRepository.setInternetStatus(InternetStatus.OFF)
        mainViewModel.refreshMovieFeed(RefreshType.Force)
        assertThat(mainViewModel.feedMovie.value?.error).isNotNull()
    }

    @Test
    fun `force movie feed refresh without internet and with filled cache,should return data`() =
        runBlockingTest {
            manualFilingMovieCache()
            fakeRepository.setInternetStatus(InternetStatus.OFF)
            mainViewModel.refreshMovieFeed(RefreshType.Force)
            assertThat(mainViewModel.feedMovie.value?.data).isEqualTo(dummyMovieApisResponse.movies)
        }

    @Test
    fun `normal movie feed refresh with empty cache,should return empty list`() =
        runBlockingTest {
            mainViewModel.refreshMovieFeed(RefreshType.Normal)
            assertThat(mainViewModel.feedMovie.value?.data).isEmpty()
        }

    @Test
    fun `normal movie feed refresh with filled cache,should return data`() =
        runBlockingTest {
            manualFilingMovieCache()
            mainViewModel.refreshMovieFeed(RefreshType.Normal)
            assertThat(mainViewModel.feedMovie.value?.data).isEqualTo(dummyMovieApisResponse.movies)
        }

    @Test
    fun `search mode movie feed refresh with query, should all return dates`() = runBlockingTest {
        mainViewModel.refreshMovieFeed(RefreshType.Search("Shawshank"))
        assertThat(mainViewModel.feedMovie.value?.data?.get(0)).isEqualTo(dummyMovieApisResponse.movies[0])
    }

    @Test
    fun `search mode movie feed refresh with empty query, should all return dates`() =
        runBlockingTest {
            mainViewModel.refreshMovieFeed(RefreshType.Search(""))
            assertThat(mainViewModel.feedMovie.value?.data).isEqualTo(dummyMovieApisResponse.movies)
        }

    @Test
    fun `search mode movie feed refresh without internet,should return error`() = runBlockingTest {
        fakeRepository.setInternetStatus(InternetStatus.OFF)
        mainViewModel.refreshMovieFeed(RefreshType.Search(""))
        assertThat(mainViewModel.feedMovie.value?.error).isNotNull()
    }

    @Test
    fun `search mode movie feed refresh without internet and with filled cache,should return data`() =
        runBlockingTest {
            manualFilingMovieCache()
            fakeRepository.setInternetStatus(InternetStatus.OFF)
            mainViewModel.refreshMovieFeed(RefreshType.Search(""))
            assertThat(mainViewModel.feedMovie.value?.data).isEqualTo(dummyMovieApisResponse.movies)
        }


    /**
     * MOVIE DETAIL RESPONSE TESTS.
     * */

    @Test
    fun `get movie detail response,should return data`() =
        runBlockingTest {
            mainViewModel.getMovieDetail("1")
            println(mainViewModel.movieDetailResponse.first())
            assertThat(mainViewModel.movieDetailResponse.first()?.data).isEqualTo(
                dummyGetMovieDetailApiResponse
            )
        }

    @Test
    fun `get movie detail response without internet,should return error`() =
        runBlockingTest {
            fakeRepository.setInternetStatus(InternetStatus.OFF)
            mainViewModel.getMovieDetail("1")
            println(mainViewModel.movieDetailResponse.first())
            assertThat(mainViewModel.movieDetailResponse.first()?.error).isNotNull()
        }


    /**
     * FAVORITE MOVIE TESTS
     * */
    @Test
    fun `insert favorite movie,should return in allMovieAndFavoriteMovie`() = runBlockingTest {

        manualFilingMovieCache()
        mainViewModel.saveFavoriteMovie(favoriteMovie)

        val movieAndFavoriteMovie = mainViewModel.allMovieAndFavoriteMovie.first().first()

        assertThat(movieAndFavoriteMovie.movie).isEqualTo(dummyMovieApisResponse.movies.first())
        assertThat(movieAndFavoriteMovie.favoriteMovie).isEqualTo(favoriteMovie)
    }

    @Test
    fun `delete favorite movie,should not be exist inside allMovieAndFavoriteMovie`() =
        runBlockingTest {

            manualFilingMovieCache()
            mainViewModel.saveFavoriteMovie(favoriteMovie)

            mainViewModel.deleteFavoriteMovie(favoriteMovie.movieId)

            assertThat(mainViewModel.allMovieAndFavoriteMovie.first()).isEmpty()

        }

}