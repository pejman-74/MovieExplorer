package com.movie_explorer.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.movie_explorer.data.database.MovieDataBase
import com.movie_explorer.data.database.dao.FavoriteMovieDao
import com.movie_explorer.data.database.dao.MovieDao
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.data.model.MovieApiResponse
import com.movie_explorer.di.TestDBQualifier
import com.movie_explorer.utils.androidDummySuccessApiResponse
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@ExperimentalCoroutinesApi
@SmallTest
class FavoriteMovieDaoTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @TestDBQualifier
    lateinit var movieDataBase: MovieDataBase


    lateinit var movieDao: MovieDao


    lateinit var favoriteMovieDao: FavoriteMovieDao

    private val dummyMovieApisResponse: MovieApiResponse =
        Gson().fromJson(androidDummySuccessApiResponse, MovieApiResponse::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
        movieDao = movieDataBase.movieDao()
        favoriteMovieDao = movieDataBase.favoriteMovieDao()
    }

    @After
    fun teraDown() {
        movieDataBase.close()
    }


    @Test
    fun check_relation_between_movie_and_favoriteMovie() = runBlockingTest {

        movieDao.insertMovie(dummyMovieApisResponse.movies)

        val favoriteMovie = FavoriteMovie(1)
        favoriteMovieDao.insertFavoriteMovie(favoriteMovie)

        val favoriteMovies =
            favoriteMovieDao.getAllMovieAndFavoriteMovie().first()

        assertThat(favoriteMovies.first().movie).isEqualTo(dummyMovieApisResponse.movies.first())
        assertThat(favoriteMovies.first().favoriteMovie).isEqualTo(favoriteMovie)
    }
}