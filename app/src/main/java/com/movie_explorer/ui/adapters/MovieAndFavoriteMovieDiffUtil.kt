package com.movie_explorer.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.movie_explorer.data.database.MovieAndFavoriteMovie


class MovieAndFavoriteMovieDiffUtil : DiffUtil.ItemCallback<MovieAndFavoriteMovie>() {

    override fun areItemsTheSame(oldItem: MovieAndFavoriteMovie, newItem: MovieAndFavoriteMovie) =
        oldItem.favoriteMovie.movieId == newItem.favoriteMovie.movieId

    override fun areContentsTheSame(
        oldItem: MovieAndFavoriteMovie,
        newItem: MovieAndFavoriteMovie
    ) = oldItem == newItem

}