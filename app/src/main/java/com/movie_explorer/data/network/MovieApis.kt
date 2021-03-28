package com.movie_explorer.data.network

import com.movie_explorer.data.model.MovieApiResponse
import com.movie_explorer.data.model.MovieDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApis {
    @GET("movies/")
    suspend fun searchMovie(@Query("q") query: String?): MovieApiResponse

    @GET("movies/{movie_id}")
    suspend fun getMovieDetail(@Path("movie_id") movie_id: String): MovieDetail
}