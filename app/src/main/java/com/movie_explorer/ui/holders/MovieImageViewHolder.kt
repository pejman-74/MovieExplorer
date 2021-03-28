package com.movie_explorer.ui.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.movie_explorer.databinding.MovieImagesItemBinding

class MovieImageViewHolder(private val movieImagesItemBinding: MovieImagesItemBinding) :
    RecyclerView.ViewHolder(movieImagesItemBinding.root) {

    fun bind(imageUrl: String) {
        movieImagesItemBinding.ivMovieImage.load(imageUrl)
    }

    companion object {
        fun from(viewGroup: ViewGroup): MovieImageViewHolder {
            val context = viewGroup.context
            val layoutInflater = LayoutInflater.from(context)
            val movieImagesItemBinding =
                MovieImagesItemBinding.inflate(layoutInflater, viewGroup, false)
            return MovieImageViewHolder(movieImagesItemBinding)
        }
    }
}