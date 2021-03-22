package com.movie_explorer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.movie_explorer.databinding.FragmentFavoriteBinding


class FavoriteFragment : Fragment() {

    lateinit var vBinding: FragmentFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vBinding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return vBinding.root
    }

}