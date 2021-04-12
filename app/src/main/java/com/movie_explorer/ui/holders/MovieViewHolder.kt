package com.movie_explorer.ui.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.movie_explorer.R
import com.movie_explorer.data.model.Movie
import com.movie_explorer.databinding.MovieItemBinding
import com.movie_explorer.utils.Utils.toGenresStyleText

class MovieViewHolder(private val movieItemBinding: MovieItemBinding) :
    RecyclerView.ViewHolder(movieItemBinding.root) {

    fun bind(movie: Movie) {
        movieItemBinding.tvTitle.text = movie.title
        movieItemBinding.tvGenres.text = movie.genres.toGenresStyleText()
        movieItemBinding.tvYear.text = movie.year
        movieItemBinding.tvCountry.text = movie.country
        movieItemBinding.tvImdb.text = movie.imdbRating
        movieItemBinding.ivPoster.load(movie.poster){
                error(R.drawable.ic_broken_image)
        }
    }

    companion object {
        fun from(viewGroup: ViewGroup): MovieViewHolder {
            val inflater = LayoutInflater.from(viewGroup.context)
            val movieViewHolder = MovieItemBinding.inflate(inflater, viewGroup, false)
            return MovieViewHolder(movieViewHolder)
        }
    }
}