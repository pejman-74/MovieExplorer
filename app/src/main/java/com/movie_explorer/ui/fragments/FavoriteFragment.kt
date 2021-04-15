package com.movie_explorer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import com.movie_explorer.R
import com.movie_explorer.data.database.MovieAndFavoriteMovie
import com.movie_explorer.databinding.FragmentFavoriteBinding
import com.movie_explorer.ui.adapters.FavoriteMovieAdapter
import com.movie_explorer.utils.Utils.showSnackBar
import com.movie_explorer.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _vBinding: FragmentFavoriteBinding? = null
    private val vBinding get() = _vBinding!!

    private val vModel: MainViewModel by activityViewModels()
    private val favoriteMovieAdapter by lazy { FavoriteMovieAdapter(requireActivity(), vModel) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _vBinding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        setUpObservers()

    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vModel.allMovieAndFavoriteMovie.collect {
                if (it.isEmpty())
                    vBinding.imBookmark.visibility = View.VISIBLE
                else
                    vBinding.imBookmark.visibility = View.GONE

                val favList: List<MovieAndFavoriteMovie> =
                    it.sortedBy { movieAndFavoriteMovie -> movieAndFavoriteMovie.favoriteMovie.createTime }

                favoriteMovieAdapter.submitList(favList)
            }
        }
    }

    private fun setUpRecyclerView() {

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(holder: RecyclerView.ViewHolder, direction: Int) {
                val favMovie =
                    favoriteMovieAdapter.currentList[holder.adapterPosition].favoriteMovie
                vModel.deleteFavoriteMovie(favMovie.movieId)
                requireView().showSnackBar(
                    message = getString(R.string.deleted), actionTitle = getString(R.string.undo)
                ) {
                    vModel.saveFavoriteMovie(favMovie)
                }

            }

        }
        vBinding.rvBookmark.apply {
            adapter = favoriteMovieAdapter
            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _vBinding = null
    }
}