package com.movie_explorer.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.movie_explorer.data.database.MovieDataBase
import com.movie_explorer.data.database.dao.MovieDao
import com.movie_explorer.data.model.MovieApiResponse
import com.movie_explorer.di.TestDBQualifier
import com.movie_explorer.utils.dummySuccessApiResponse
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@ExperimentalCoroutinesApi
@SmallTest
class MovieDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    @TestDBQualifier
    lateinit var movieDataBase: MovieDataBase

    lateinit var movieDao: MovieDao

    private val dummyMovieApisResponse: MovieApiResponse =
        Gson().fromJson(dummySuccessApiResponse, MovieApiResponse::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
        movieDao = movieDataBase.movieDao()
    }

    @After
    fun teraDown() {
        movieDataBase.close()
    }

    @Test
    fun insert_dummy_movie() = runBlockingTest {
        movieDao.insertMovie(dummyMovieApisResponse.movies)
        assertThat(movieDao.getAllMovies()).isEqualTo(dummyMovieApisResponse.movies)
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