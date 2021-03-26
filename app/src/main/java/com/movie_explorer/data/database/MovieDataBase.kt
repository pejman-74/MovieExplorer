package com.movie_explorer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.movie_explorer.data.database.dao.MovieDao
import com.movie_explorer.data.model.Movie

@Database(entities = [Movie::class], version = 1)
@TypeConverters(DBTypeConverter::class)
abstract class MovieDataBase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
}