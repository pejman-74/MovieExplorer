package com.movie_explorer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.movie_explorer.data.database.dao.FavoriteMovieDao
import com.movie_explorer.data.database.dao.MovieDao
import com.movie_explorer.data.database.dao.MovieDetailDao
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.data.model.Movie
import com.movie_explorer.data.model.MovieDetail

@Database(entities = [Movie::class, FavoriteMovie::class, MovieDetail::class], version = 1)
@TypeConverters(DBTypeConverter::class)
abstract class MovieDataBase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
    abstract fun favoriteMovieDao(): FavoriteMovieDao
    abstract fun movieDetailDao(): MovieDetailDao
}