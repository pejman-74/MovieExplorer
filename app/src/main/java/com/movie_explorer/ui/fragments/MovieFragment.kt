package com.movie_explorer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.movie_explorer.databinding.FragmentMovieBinding
import com.movie_explorer.viewmodel.MainViewModel


class MovieFragment : Fragment() {

    private lateinit var vBinding: FragmentMovieBinding
    private val vModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vBinding = FragmentMovieBinding.inflate(inflater, container, false)
        return vBinding.root
    }


}