package com.movie_explorer.data.model


import com.google.gson.annotations.SerializedName

data class MovieApiResponse(
    @SerializedName("data")
    val movies: List<Movie>,
    @SerializedName("metadata")
    val metadata: Metadata? = null
)