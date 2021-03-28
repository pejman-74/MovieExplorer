package com.movie_explorer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.movie_explorer.R
import com.movie_explorer.databinding.FragmentDetailBinding
import com.movie_explorer.ui.adapters.MovieImageAdapter
import com.movie_explorer.utils.showLongToast
import com.movie_explorer.viewmodel.MainViewModel
import com.movie_explorer.wrapper.ResourceResult


class DetailFragment : Fragment() {

    private lateinit var vBinding: FragmentDetailBinding
    private val vModel: MainViewModel by activityViewModels()
    private val movieImagesAdapter by lazy { MovieImageAdapter() }
    private val navArgs: DetailFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vBinding = FragmentDetailBinding.inflate(inflater, container, false)
        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vBinding.vpMovieImages.apply {
            adapter = movieImagesAdapter
        }
        vModel.movieDetailResponse.observe(viewLifecycleOwner, { result ->
            when (result) {
                is ResourceResult.Failure -> {
                    requireContext().showLongToast(getString(R.string.unknown_error))
                }
                is ResourceResult.Loading -> Unit
                is ResourceResult.Success -> {
                    val movieDetail = result.value
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
                }
            }

        })

        vModel.getMovieDetail(navArgs.movieId.toString())
    }


}