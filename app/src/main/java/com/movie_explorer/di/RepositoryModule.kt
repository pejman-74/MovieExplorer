package com.movie_explorer.di

import com.movie_explorer.data.database.dao.FavoriteMovieDao
import com.movie_explorer.data.database.dao.MovieDao
import com.movie_explorer.data.database.dao.MovieDetailDao
import com.movie_explorer.data.network.MovieApis
import com.movie_explorer.data.repository.Repository
import com.movie_explorer.data.repository.RepositoryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Provides
    @Singleton
    fun provideRepository(
        movieApis: MovieApis,
        movieDao: MovieDao,
        favoriteMovieDao: FavoriteMovieDao,
        movieDetailDao: MovieDetailDao
    ): RepositoryInterface = Repository(movieApis, movieDao, favoriteMovieDao, movieDetailDao)
}