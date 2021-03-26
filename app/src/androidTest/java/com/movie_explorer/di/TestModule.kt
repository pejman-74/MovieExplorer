package com.movie_explorer.di

import android.content.Context
import androidx.room.Room
import com.movie_explorer.data.database.MovieDataBase
import com.movie_explorer.data.database.dao.MovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier


@InstallIn(SingletonComponent::class)
@Module
object TestModule {
    @Provides
    @TestDBQualifier
    fun provideTestDB(@ApplicationContext context: Context): MovieDataBase =
        Room.inMemoryDatabaseBuilder(context, MovieDataBase::class.java).allowMainThreadQueries()
            .build()

    @Provides
    @TestMovieDaoQualifier
    fun provideTestMovieDao(@TestDBQualifier movieDataBase: MovieDataBase): MovieDao =
        movieDataBase.movieDao()


}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TestDBQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TestMovieDaoQualifier
