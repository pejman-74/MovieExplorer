package com.movie_explorer.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.movie_explorer.R
import com.movie_explorer.databinding.FragmentMovieBinding
import com.movie_explorer.ui.adapters.MovieAdapter
import com.movie_explorer.utils.showLongToast
import com.movie_explorer.viewmodel.MainViewModel
import com.movie_explorer.wrapper.ResourceResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFragment : Fragment(), SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private lateinit var vBinding: FragmentMovieBinding
    private val vModel: MainViewModel by activityViewModels()
    private val movieAdapter by lazy { MovieAdapter() }
    private lateinit var searchView: SearchView
    private var lastQuery: String? = null
    private var isInExceptionMode = false
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

        //observing search result (cache and network)
        vModel.movieSearchResponse.observe(viewLifecycleOwner, {
            when (it) {
                is ResourceResult.Failure ->
                    requireContext().showLongToast(getString(R.string.unknown_error))
                ResourceResult.Loading -> {
                }
                is ResourceResult.Success -> {
                    val movies = it.value.movies
                    movieAdapter.setMovieList(movies)
                    lastQuery = null
                }
            }
        })

        //observing all cached movies
        vModel.allCacheMovies.observe(viewLifecycleOwner, {
            /**
            if cache is empty and connection not available,mean is:
            user lunched the app for first time without internet connection.
             */
            isInExceptionMode = if (it.isEmpty() && !vModel.hasInternetConnection()) {
                requireContext().showLongToast(getString(R.string.enable_internet))
                true
            } else {
                false
            }
            //if cache is empty then searching for get initializing movies
            if (it.isEmpty()) {
                vModel.searchMovie()
                return@observe
            }
            //set data if recyclerView is empty (initial movie for app lunch time)
            if (movieAdapter.itemCount == 0)
                movieAdapter.setMovieList(it)
        })

        //observing live internet connection
        vModel.hasInternetConnectionLive.observe(viewLifecycleOwner, { hasConnection ->
            //if cache is empty and connected to internet searching for get initializing movies
            if (hasConnection == true && vModel.allCacheMovies.value.isNullOrEmpty())
            //check user have previous search otherwise search initializing
                if (lastQuery.isNullOrBlank())
                    vModel.searchMovie()
                else
                    vModel.searchMovie(lastQuery)
        })

    }


    override fun onQueryTextSubmit(query: String?): Boolean {

        //save query
        lastQuery = query

        if (isInExceptionMode) {
            requireContext().showLongToast(getString(R.string.enable_internet))
            return true
        }
        query?.let {
            vModel.searchMovie(it)
            //scroll to top
            if (movieAdapter.itemCount > 0)
                vBinding.rvMovies.scrollToPosition(0)
        }
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)
        searchView.setOnCloseListener(this)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun onClose(): Boolean {
        vModel.allCacheMovies.value?.let { movieAdapter.setMovieList(it) }
        return false
    }
}