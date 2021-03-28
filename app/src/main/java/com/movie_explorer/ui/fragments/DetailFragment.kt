package com.movie_explorer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.movie_explorer.databinding.FragmentDetailBinding
import com.movie_explorer.ui.adapters.MovieImageAdapter
import com.movie_explorer.viewmodel.MainViewModel


class DetailFragment : Fragment() {

    private lateinit var vBinding: FragmentDetailBinding
    private val vModel: MainViewModel by activityViewModels()
    private val movieImagesAdapter by lazy { MovieImageAdapter() }
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
movieImagesAdapter.setUrls(listOf("http://moviesapi.ir/images/tt0111161_screenshot1.jpg",
    "http://moviesapi.ir/images/tt0111161_screenshot2.jpg",
    "http://moviesapi.ir/images/tt0111161_screenshot3.jpg"))

    }
}