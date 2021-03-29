package com.movie_explorer.ui.adapters

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.movie_explorer.R
import com.movie_explorer.data.model.Movie
import com.movie_explorer.ui.fragments.FavoriteFragmentDirections
import com.movie_explorer.ui.holders.MovieViewHolder
import com.movie_explorer.utils.doubleButtonAlertDialog
import com.movie_explorer.viewmodel.MainViewModel

class FavoriteMovieAdapter(
    private val requireActivity: FragmentActivity,
    private val mainViewModel: MainViewModel
) : RecyclerView.Adapter<MovieViewHolder>(), ActionMode.Callback {
    private val movieDiffCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem == newItem
    }
    private val movieListDiffer = AsyncListDiffer(this, movieDiffCallback)
    private val movies: List<Movie> get() = movieListDiffer.currentList
    private var actionMode: ActionMode? = null

    private val selectedMoviesId by lazy { ArrayList<Int>() }
    private val selectedMovieViewHolders by lazy { HashSet<MovieViewHolder>() }


    fun setMovieList(movies: List<Movie>) {
        movieListDiffer.submitList(movies)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
        holder.itemView.setOnLongClickListener {
            if (actionMode == null) {
                requireActivity.startActionMode(this)
                setSelectItem(holder, movie.id)
            }
            true
        }
        holder.itemView.setOnClickListener {
            //check in selecting mode
            if (actionMode == null) {
                it.findNavController()
                    .navigate(FavoriteFragmentDirections.toDetailFragment(movie.id))
            } else {
                if (movie.id in selectedMoviesId)
                    setDeselectItem(holder, movie.id)
                else
                    setSelectItem(holder, movie.id)
            }
        }
    }

    override fun getItemCount(): Int = movies.size

    private fun setActionBarTitle() {
        selectedMoviesId.size.let {
            if (it > 0)
                actionMode?.title = "$it"
            else
                exitActionMode()
        }
    }

    private fun setSelectItem(holder: MovieViewHolder, movieId: Int) {
        setViewHolderBackgroundColor(holder, R.color.movieItemSelectedColor)
        selectedMoviesId.add(movieId)
        setActionBarTitle()
        selectedMovieViewHolders.add(holder)
    }

    private fun setDeselectItem(holder: MovieViewHolder, movieId: Int) {
        setViewHolderBackgroundColor(holder, R.color.movieItemBackgroundColor)
        selectedMoviesId.remove(movieId)
        setActionBarTitle()
        selectedMovieViewHolders.remove(holder)
    }

    private fun setViewHolderBackgroundColor(holder: MovieViewHolder, colorId: Int) {
        holder.itemView.findViewById<MaterialCardView>(R.id.cv_movie_item)
            .setCardBackgroundColor(ContextCompat.getColor(requireActivity, colorId))
    }

    private fun exitActionMode() {
        selectedMovieViewHolders.forEach {
            setViewHolderBackgroundColor(it, R.color.movieItemBackgroundColor)
        }
        selectedMoviesId.clear()
        selectedMovieViewHolders.clear()
        actionMode?.finish()
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.favorit_movie_action_mode_menu, menu)
        actionMode = mode
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = true

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.delete_favorite_items -> {
                if (selectedMoviesId.isNotEmpty())
                    requireActivity.doubleButtonAlertDialog(
                        requireActivity.getString(R.string.delete_favorite_dialog_title),
                        requireActivity.getString(R.string.delete_favorite_dialog_message),
                        requireActivity.getString(R.string.delete_favorite_dialog_positive_button),
                        requireActivity.getString(R.string.delete_favorite_dialog_negative_button),
                        {
                            selectedMoviesId.forEach { id ->
                                mainViewModel.deleteFavoriteMovie(id)
                            }
                            exitActionMode()
                        })
            }
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        exitActionMode()
    }
}