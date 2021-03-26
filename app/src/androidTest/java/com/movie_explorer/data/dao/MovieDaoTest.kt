package com.movie_explorer.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.movie_explorer.data.database.dao.MovieDao
import com.movie_explorer.data.model.MovieApiResponse
import com.movie_explorer.di.TestMovieDaoQualifier
import com.movie_explorer.utils.androidDummySuccessApiResponse
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MovieDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    @TestMovieDaoQualifier
    lateinit var movieDao: MovieDao


    private val dummyMovieApisResponse: MovieApiResponse =
        Gson().fromJson(androidDummySuccessApiResponse, MovieApiResponse::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun insert_dummy_movie() = runBlockingTest {
        movieDao.insertMovie(dummyMovieApisResponse.movies)
        assertThat(movieDao.getAllMovies().first()).isEqualTo(dummyMovieApisResponse.movies)
    }

    @Test
    fun search_movie_by_name() = runBlockingTest {
        movieDao.insertMovie(dummyMovieApisResponse.movies)
        assertThat(movieDao.searchMovieByName("The Shawshank Redemption").size).isEqualTo(1)
    }

    @Test
    fun search_movie_by_name_with_empty_parameter_should_return_all_movies() = runBlockingTest {
        movieDao.insertMovie(dummyMovieApisResponse.movies)
        assertThat(movieDao.searchMovieByName("").size).isEqualTo(dummyMovieApisResponse.movies.size)
    }
}