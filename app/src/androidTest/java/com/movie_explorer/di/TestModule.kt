package com.movie_explorer.di

import android.content.Context
import androidx.room.Room
import com.movie_explorer.data.database.MovieDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object TestModule {
    @Provides
    @TestDBQualifier
    fun provideTestDB(@ApplicationContext context: Context): MovieDataBase =
        Room.inMemoryDatabaseBuilder(context, MovieDataBase::class.java).allowMainThreadQueries()
            .enableMultiInstanceInvalidation()
            .build()
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TestDBQualifier


