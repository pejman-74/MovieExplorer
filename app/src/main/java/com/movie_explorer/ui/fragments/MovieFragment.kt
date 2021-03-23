package com.movie_explorer.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.movie_explorer.R
import com.movie_explorer.databinding.FragmentMovieBinding
import com.movie_explorer.ui.adapters.MovieAdapter
import com.movie_explorer.viewmodel.MainViewModel
import com.movie_explorer.wrapper.ResourceResult


class MovieFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var vBinding: FragmentMovieBinding
    private val vModel: MainViewModel by activityViewModels()
    private val movieAdapter by lazy { MovieAdapter() }
    private lateinit var searchView: SearchView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vBinding = FragmentMovieBinding.inflate(inflater, container, false)
        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        vBinding.rvMovies.apply {
            adapter = movieAdapter
        }
        vModel.movieSearchResponse.observe(viewLifecycleOwner, {
            when (it) {
                is ResourceResult.Failure -> TODO()
                ResourceResult.Loading -> {
                }
                is ResourceResult.Success -> {
                    movieAdapter.setMovieList(it.value.movies)
                }
            }
        })
        vModel.searchMovie("a")
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { vModel.searchMovie(it) }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }
}