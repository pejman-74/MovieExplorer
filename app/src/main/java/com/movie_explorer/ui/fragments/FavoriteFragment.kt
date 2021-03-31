package com.movie_explorer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.movie_explorer.data.model.Movie
import com.movie_explorer.databinding.FragmentFavoriteBinding
import com.movie_explorer.ui.adapters.FavoriteMovieAdapter
import com.movie_explorer.viewmodel.MainViewModel


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

        vBinding.rvBookmark.apply {
            adapter = favoriteMovieAdapter
        }

        vModel.allMovieAndFavoriteMovie.observe(viewLifecycleOwner, {
            if (it.isEmpty())
                vBinding.imBookmark.visibility = View.VISIBLE

            val movies: List<Movie> =
                it.sortedBy { movieAndFavoriteMovie -> movieAndFavoriteMovie.favoriteMovie.createTime }
                    .map { movieAndFavoriteMovie ->
                        movieAndFavoriteMovie.movie
                    }
            favoriteMovieAdapter.setMovieList(movies)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _vBinding
    }
}