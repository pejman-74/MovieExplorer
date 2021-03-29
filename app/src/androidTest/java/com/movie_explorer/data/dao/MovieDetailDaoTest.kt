package com.movie_explorer.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.movie_explorer.data.database.MovieDataBase
import com.movie_explorer.data.database.dao.MovieDetailDao
import com.movie_explorer.data.model.MovieDetail
import com.movie_explorer.di.TestDBQualifier
import com.movie_explorer.utils.androidDummySuccessGetMovieDetailApiResponse
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltAndroidTest
@SmallTest
class MovieDetailDaoTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @TestDBQualifier
    lateinit var movieDataBase: MovieDataBase
    lateinit var movieDetailDao: MovieDetailDao

    private val dummyMovieDetailApiResponse: MovieDetail =
        Gson().fromJson(androidDummySuccessGetMovieDetailApiResponse, MovieDetail::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
        movieDetailDao = movieDataBase.movieDetailDao()
    }

    @After
    fun tearDown() {
        movieDataBase.close()
    }

    @Test
    fun test_Insert() = runBlockingTest {
        movieDetailDao.insertMovieDetail(dummyMovieDetailApiResponse)
        assertThat(movieDetailDao.getMovieDetail(1)).isEqualTo(dummyMovieDetailApiResponse)
    }

}