package com.movie_explorer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class FavoriteMovie(
    @PrimaryKey(autoGenerate = false)
    val movieId: Int
)