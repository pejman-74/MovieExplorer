package com.movie_explorer.di

import android.content.Context
import com.movie_explorer.data.database.MovieDataBase
import com.movie_explorer.data.database.dao.FavoriteMovieDao
import com.movie_explorer.data.database.dao.MovieDao
import com.movie_explorer.data.network.MovieApis
import com.movie_explorer.data.repository.Repository
import com.movie_explorer.data.repository.RepositoryInterface
import com.movie_explorer.utils.Constants
import com.movie_explorer.utils.network.ConnectionCheckerInterface
import com.movie_explorer.utils.network.LiveConnectionObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS).build()


    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(gsonConverterFactory).client(okHttpClient).build()

    @Provides
    @Singleton
    fun provideMovieApis(retrofit: Retrofit): MovieApis = retrofit.create(MovieApis::class.java)


    @Provides
    @Singleton
    fun provideRepository(movieApis: MovieApis, movieDao: MovieDao,favoriteMovieDao: FavoriteMovieDao): RepositoryInterface =
        Repository(movieApis, movieDao,favoriteMovieDao)

    @Provides
    @Singleton
    fun provideConnectionChecker(@ApplicationContext context: Context): ConnectionCheckerInterface =
        LiveConnectionObserver(context)

}