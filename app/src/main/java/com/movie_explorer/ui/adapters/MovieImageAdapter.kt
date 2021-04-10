package com.movie_explorer.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.movie_explorer.ui.holders.MovieImageViewHolder

class MovieImageAdapter : ListAdapter<String, MovieImageViewHolder>(StringDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieImageViewHolder {
        return MovieImageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MovieImageViewHolder, position: Int) {
        val url = getItem(position)
        holder.bind(url)
    }

}