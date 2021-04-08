package com.movie_explorer.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.movie_explorer.R
import com.movie_explorer.databinding.FragmentMovieBinding
import com.movie_explorer.ui.adapters.MovieAdapter
import com.movie_explorer.viewmodel.MainViewModel
import com.movie_explorer.wrapper.RefreshType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MovieFragment : Fragment(), SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private var _vBinding: FragmentMovieBinding? = null
    private val vBinding get() = _vBinding!!

    private val vModel: MainViewModel by activityViewModels()
    private val movieAdapter by lazy { MovieAdapter() }
    private lateinit var searchView: SearchView
    private var lastQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _vBinding = FragmentMovieBinding.inflate(inflater, container, false)
        return vBinding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        vBinding.rvMovies.apply {
            adapter = movieAdapter
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vModel.feedMovie.collect {
                val result = if (it == null) {
                    vBinding.rvMovies.showShimmer()
                    return@collect
                } else {
                    vBinding.rvMovies.hideShimmer()
                    it
                }
                vBinding.failingView.root.isVisible =
                    result.error != null && result.data.isNullOrEmpty()

                movieAdapter.submitList(result.data) {
                    //scroll if user searched
                    if (lastQuery != null)
                        scrollTop()
                }
            }
        }


        vBinding.failingView.btnRetry.setOnClickListener {
            if (!lastQuery.isNullOrBlank())
                vModel.refreshMovieFeed(RefreshType.Search(lastQuery!!))
            else
                vModel.refreshMovieFeed(RefreshType.Force)
        }

    }


    override fun onStart() {
        super.onStart()
        if (vModel.feedMovie.value == null)
            vModel.refreshMovieFeed(RefreshType.Force)
    }

    //Scroll to top if user searching
    private fun scrollTop() = lifecycleScope.launch {
        if (movieAdapter.itemCount > 0)
            vBinding.rvMovies.scrollToPosition(0)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        //save query
        lastQuery = query

        query?.let { vModel.refreshMovieFeed(RefreshType.Search(it)) }
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
        lastQuery = null
        vModel.refreshMovieFeed(RefreshType.Normal)
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _vBinding=null
    }
}