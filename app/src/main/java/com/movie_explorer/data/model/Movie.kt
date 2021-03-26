package com.movie_explorer.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Movie(
    @SerializedName("country")
    val country: String,
    @SerializedName("genres")
    val genres: List<String>,
    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @SerializedName("images")
    val images: List<String>? = null,
    @SerializedName("imdb_rating")
    val imdbRating: String,
    @SerializedName("poster")
    val poster: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("year")
    val year: String
)