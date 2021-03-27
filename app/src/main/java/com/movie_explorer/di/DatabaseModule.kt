package com.movie_explorer.di

import android.content.Context
import androidx.room.Room
import com.movie_explorer.data.database.MovieDataBase
import com.movie_explorer.data.database.dao.FavoriteMovieDao
import com.movie_explorer.data.database.dao.MovieDao
import com.movie_explorer.utils.Constants.Companion.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMovieDataBase(@ApplicationContext context: Context): MovieDataBase =
        Room.databaseBuilder(context, MovieDataBase::class.java, DB_NAME).build()

    @Provides
    @Singleton
    fun provideMovieDao(movieDataBase: MovieDataBase): MovieDao = movieDataBase.movieDao()

    @Provides
    @Singleton
    fun provideFavoriteMoviesDao(movieDataBase: MovieDataBase): FavoriteMovieDao = movieDataBase.favoriteMovieDao()
}