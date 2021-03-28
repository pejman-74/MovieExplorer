package com.movie_explorer.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.movie_explorer.ui.holders.MovieImageViewHolder

class MovieImageAdapter : RecyclerView.Adapter<MovieImageViewHolder>() {

    private val urlDiffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }
    private val urlListDiffer = AsyncListDiffer(this, urlDiffCallback)
    private val urls: List<String> get() = urlListDiffer.currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieImageViewHolder {
        return MovieImageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MovieImageViewHolder, position: Int) {
        holder.bind(urls[position])
    }

    override fun getItemCount(): Int = urls.size

    fun setUrls(urls: List<String>) {
        urlListDiffer.submitList(urls)
    }

}