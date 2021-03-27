package com.movie_explorer.data.database

import androidx.room.Embedded
import androidx.room.Relation
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.data.model.Movie

data class MovieAndFavoriteMovie(
    /**
    * Room database @Relation annotation not working,i don't know why.
    * making relation manually with sql query
    * */
    @Embedded val movie: Movie,
    @Embedded val favoriteMovie: FavoriteMovie
)