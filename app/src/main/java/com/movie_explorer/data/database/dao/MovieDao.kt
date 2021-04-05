package com.movie_explorer.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.movie_explorer.data.model.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movies: List<Movie>)

    @Query("SELECT * FROM MOVIE WHERE title like '%' || :query ||'%' ")
    fun searchMovieByName(query: String): Flow<List<Movie>>

    @Query("SELECT * FROM MOVIE")
    fun getAllMovies(): Flow<List<Movie>>
}