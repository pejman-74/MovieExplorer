package com.movie_explorer.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.movie_explorer.R
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.data.model.MovieDetail
import com.movie_explorer.databinding.FailingLayoutBinding
import com.movie_explorer.databinding.FragmentDetailBinding
import com.movie_explorer.databinding.FragmentDetailPlaceHolderBinding
import com.movie_explorer.ui.MainActivity
import com.movie_explorer.ui.adapters.MovieImageAdapter
import com.movie_explorer.utils.getCurrentUTCDateTime
import com.movie_explorer.utils.interceptor.NoInternetException
import com.movie_explorer.utils.showLongToast
import com.movie_explorer.viewmodel.MainViewModel
import com.movie_explorer.wrapper.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _shimmerViewBinding: FragmentDetailPlaceHolderBinding? = null
    private val shimmerViewBinding get() = _shimmerViewBinding!!
    private var _vBinding: FragmentDetailBinding? = null
    private val vBinding get() = _vBinding!!
    private var _failingViewBinding: FailingLayoutBinding? = null
    private val failingViewBinding get() = _failingViewBinding!!

    private val vModel: MainViewModel by activityViewModels()
    private val movieImagesAdapter by lazy { MovieImageAdapter() }
    private val navArgs: DetailFragmentArgs by navArgs()
    private lateinit var menuItem: MenuItem
    private var isFavored = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _vBinding = FragmentDetailBinding.inflate(inflater, container, false)
        _shimmerViewBinding = FragmentDetailPlaceHolderBinding.inflate(inflater, container, false)
        _failingViewBinding = FailingLayoutBinding.inflate(inflater, container, false)
        return shimmerViewBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        vBinding.vpMovieImages.apply {
            adapter = movieImagesAdapter
            vBinding.dotsIndicator.setViewPager2(this)
        }
        failingViewBinding.btnRetry.setOnClickListener {
            loadMovieDetail()
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vModel.movieDetailResponse.collect { result ->
                when (result) {
                    is Resource.Error -> {
                        showFailingLayout()
                        when (result.error!!) {
                            is NoInternetException ->
                                requireContext().showLongToast(getString(R.string.enable_internet))
                            else ->
                                requireContext().showLongToast(getString(R.string.unknown_error))
                        }
                    }
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        setUiWithMovieDetail(result.data!!)
                        disableShimmerEffect()
                    }
                }

            }
        }

        loadMovieDetail()
    }

    private fun setUiWithMovieDetail(movieDetail: MovieDetail) {
        (requireActivity() as MainActivity).supportActionBar?.title = movieDetail.title
        movieDetail.images?.let { it -> movieImagesAdapter.submitList(it) }
        vBinding.tvRelease.text = movieDetail.released.trim()
        vBinding.tvTime.text = movieDetail.runtime.trim()
        vBinding.tvImdb.text = movieDetail.imdbRating.trim()
        vBinding.tvDirector.text = movieDetail.director.trim()
        vBinding.tvCountry.text = movieDetail.country.trim()
        vBinding.tvDescription.text =
            getString(
                R.string.movie_description,
                movieDetail.plot.trim(),
                movieDetail.actors.trim(),
                movieDetail.writer.trim(),
                movieDetail.awards.trim()
            )
    }

    private fun loadMovieDetail() = vModel.getMovieDetail(navArgs.movieId.toString())

    private fun disableShimmerEffect() {
        shimmerViewBinding.root.removeAllViews()
        shimmerViewBinding.root.addView(vBinding.root)
    }

    private fun showFailingLayout() {
        shimmerViewBinding.root.removeAllViews()
        shimmerViewBinding.root.addView(failingViewBinding.root)
    }

    private fun setIsFavoredMode(isSelected: Boolean? = null) {
        val color = if (isSelected == true) {
            setIsFavored(true)
            ContextCompat.getColor(requireContext(), R.color.favoriteIconTintSelectedColor)
        } else {
            setIsFavored(false)
            ContextCompat.getColor(requireContext(), R.color.favoriteIconTintColor)
        }
        menuItem.icon.setTint(color)

    }

    private fun setIsFavored(isFav: Boolean) {
        isFavored = isFav
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_menu, menu)
        menuItem = menu.findItem(R.id.favoriteMenuItem)
        setIsFavoredMode()
        viewLifecycleOwner.lifecycleScope.launch {
            vModel.allMovieAndFavoriteMovie.first().forEach { movieAndFavoriteMovie ->
                if (movieAndFavoriteMovie.favoriteMovie.movieId == navArgs.movieId) {
                    setIsFavoredMode(true)
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favoriteMenuItem -> {
                if (isFavored) {
                    vModel.deleteFavoriteMovie(navArgs.movieId)
                    setIsFavoredMode(false)
                } else {
                    vModel.saveFavoriteMovie(
                        FavoriteMovie(navArgs.movieId, getCurrentUTCDateTime())
                    )
                    setIsFavoredMode(true)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _shimmerViewBinding = null
        _vBinding = null
        _failingViewBinding = null
    }
}