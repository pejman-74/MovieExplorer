package com.movie_explorer.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.movie_explorer.data.model.Movie
import com.movie_explorer.ui.holders.MovieViewHolder

class MovieAdapter : RecyclerView.Adapter<MovieViewHolder>() {
    private val movieDiffCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem == newItem
    }
    private val movieListDiffer = AsyncListDiffer(this, movieDiffCallback)
    private val movies: List<Movie> get() = movieListDiffer.currentList

    fun setMovieList(movies: List<Movie>) {
        movieListDiffer.submitList(movies)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size
}