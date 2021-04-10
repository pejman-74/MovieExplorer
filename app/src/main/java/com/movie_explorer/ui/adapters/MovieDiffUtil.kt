package com.movie_explorer.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.movie_explorer.data.model.Movie


class MovieDiffUtil : DiffUtil.ItemCallback<Movie>() {

    override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem

}