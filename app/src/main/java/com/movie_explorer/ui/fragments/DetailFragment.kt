package com.movie_explorer.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.movie_explorer.R
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.databinding.FragmentDetailBinding
import com.movie_explorer.databinding.FragmentDetailPlaceHolderBinding
import com.movie_explorer.ui.MainActivity
import com.movie_explorer.ui.adapters.MovieImageAdapter
import com.movie_explorer.utils.getCurrentUTCDateTime
import com.movie_explorer.utils.observeOnce
import com.movie_explorer.utils.showLongToast
import com.movie_explorer.viewmodel.MainViewModel
import com.movie_explorer.wrapper.ResourceResult


class DetailFragment : Fragment() {

    private var _shimmerViewBinding: FragmentDetailPlaceHolderBinding? = null
    private val shimmerViewBinding get() = _shimmerViewBinding!!
    private var _vBinding: FragmentDetailBinding? = null
    private val vBinding get() = _vBinding!!

    private val vModel: MainViewModel by activityViewModels()
    private val movieImagesAdapter by lazy { MovieImageAdapter() }
    private val navArgs: DetailFragmentArgs by navArgs()
    private lateinit var menuItem: MenuItem
    private var isFavored = false
    private var hasFailingLoad = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _vBinding = FragmentDetailBinding.inflate(inflater, container, false)
        _shimmerViewBinding = FragmentDetailPlaceHolderBinding.inflate(inflater, container, false)
        return shimmerViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        vBinding.vpMovieImages.apply {
            adapter = movieImagesAdapter
            vBinding.dotsIndicator.setViewPager2(this)
        }
        vModel.movieDetailResponse.observe(viewLifecycleOwner, { result ->
            when (result) {
                is ResourceResult.Failure -> {
                    requireContext().showLongToast(getString(R.string.unknown_error))
                }
                is ResourceResult.Loading -> Unit
                is ResourceResult.Success -> {
                    //if result is null, mean is not founded in db and connection is off
                    if (result.value == null) {
                        hasFailingLoad = true
                        requireContext().showLongToast(getString(R.string.cant_load_from_cache))
                    } else {
                        hasFailingLoad = false
                        val movieDetail = result.value
                        (requireActivity() as MainActivity).supportActionBar?.title =
                            movieDetail.title
                        movieDetail.images?.let { it -> movieImagesAdapter.setUrls(it) }
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
                        disableShimmerEffect()
                    }
                }
            }

        })

        loadMovieDetail()

        vModel.hasInternetConnectionLive.observe(viewLifecycleOwner, {
            if (hasFailingLoad)
               loadMovieDetail()
        })
    }

    private fun loadMovieDetail() = vModel.getMovieDetail(navArgs.movieId)

    private fun disableShimmerEffect() {
        shimmerViewBinding.root.removeAllViews()
        shimmerViewBinding.root.addView(vBinding.root)
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
        vModel.allMovieAndFavoriteMovie.observeOnce(viewLifecycleOwner, {
            it.forEach { movieAndFavoriteMovie ->
                if (movieAndFavoriteMovie.favoriteMovie.movieId == navArgs.movieId) {
                    setIsFavoredMode(true)
                }
            }
        })
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
    }
}