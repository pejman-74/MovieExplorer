package com.movie_explorer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteMovie(
    @PrimaryKey(autoGenerate = false)
    val movieId: Int,
    val createTime: String
)