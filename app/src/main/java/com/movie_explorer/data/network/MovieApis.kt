package com.movie_explorer.data.network

import com.movie_explorer.data.model.MovieApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApis {
    @GET("movies/")
    suspend fun searchMovie(@Query("q") query: String?): MovieApiResponse
}