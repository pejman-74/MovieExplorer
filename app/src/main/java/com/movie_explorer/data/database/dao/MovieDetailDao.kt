package com.movie_explorer.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.movie_explorer.data.model.MovieDetail

@Dao
interface MovieDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetail(movieDetail: MovieDetail)

    @Query("SELECT * FROM movieDetail WHERE id=:movieDetailId")
    suspend fun getMovieDetail(movieDetailId: String): MovieDetail
}