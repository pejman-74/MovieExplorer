package com.movie_explorer.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.movie_explorer.R
import com.movie_explorer.databinding.FragmentMovieBinding
import com.movie_explorer.ui.adapters.MovieAdapter
import com.movie_explorer.utils.observeOnce
import com.movie_explorer.utils.showLongToast
import com.movie_explorer.viewmodel.MainViewModel
import com.movie_explorer.wrapper.ResourceResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieFragment : Fragment(), SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private var _vBinding: FragmentMovieBinding? = null
    private val vBinding get() = _vBinding!!

    private val vModel: MainViewModel by activityViewModels()
    private val movieAdapter by lazy { MovieAdapter() }
    private lateinit var searchView: SearchView
    private var lastQuery: String? = null
    private var isInExceptionMode = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _vBinding = FragmentMovieBinding.inflate(inflater, container, false)
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
                is ResourceResult.Failure -> {
                    vBinding.rvMovies.hideShimmer()
                    if (it.isInExceptionMode) {
                        requireContext().showLongToast(getString(R.string.enable_internet))
                        vBinding.rvMovies.hideShimmer()
                        vBinding.imDissatisfied.visibility = View.VISIBLE
                        isInExceptionMode = true

                    } else
                        requireContext().showLongToast(getString(R.string.unknown_error))
                }
                ResourceResult.Loading -> {
                    vBinding.rvMovies.showShimmer()
                }
                is ResourceResult.Success -> {

                    val movies = it.value.movies
                    movieAdapter.setMovieList(movies)

                    /*Scroll to top if user searching*/
                    if (lastQuery != null)
                        scrollTop()

                    vBinding.rvMovies.hideShimmer()
                    vBinding.imDissatisfied.visibility = View.GONE
                    lastQuery = null
                    isInExceptionMode = false
                }
            }
        })

        if (vModel.movieSearchResponse.value == null)
            vModel.searchMovie()

        //observing live internet connection
        vModel.hasInternetConnectionLive.observe(viewLifecycleOwner, { hasConnection ->

            if (hasConnection) {
                when {
                    !lastQuery.isNullOrBlank() ->
                        vModel.searchMovie(lastQuery!!)
                    isInExceptionMode ->
                        vModel.searchMovie()
                }

            }
        })

    }

    private fun scrollTop() = lifecycleScope.launch {
        delay(100)
        if (movieAdapter.itemCount > 0)
            vBinding.rvMovies.smoothScrollToPosition(0)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        //save query
        lastQuery = query

        query?.let { vModel.searchMovie(it) }
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
        vModel.allCacheMovies.observeOnce(viewLifecycleOwner, {
            movieAdapter.setMovieList(it)
            scrollTop()
        })
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _vBinding=null
    }
}