package com.movie_explorer.data.database.dao

import androidx.room.*
import com.movie_explorer.data.database.MovieAndFavoriteMovie
import com.movie_explorer.data.model.FavoriteMovie
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteMovie(favoriteMovie: FavoriteMovie)

    @Query("DELETE  FROM favoriteMovie WHERE movieId=:movieId")
    suspend fun deleteFavoriteMovie(movieId: Int)

    @Query("SELECT * FROM FavoriteMovie fm  JOIN MOVIE  m ON m.id= fm.movieId ")
    @Transaction
    fun getAllMovieAndFavoriteMovie(): Flow<List<MovieAndFavoriteMovie>>


}