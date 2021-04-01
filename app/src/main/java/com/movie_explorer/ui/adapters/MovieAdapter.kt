package com.movie_explorer.ui.adapters

import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import com.movie_explorer.data.model.Movie
import com.movie_explorer.ui.fragments.MovieFragmentDirections
import com.movie_explorer.ui.holders.MovieViewHolder

class MovieAdapter : ListAdapter<Movie,MovieViewHolder>(GenericDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
        holder.itemView.setOnClickListener {
            it.findNavController()
                .navigate(MovieFragmentDirections.toDetailFragment(movie.id))
        }
    }

}