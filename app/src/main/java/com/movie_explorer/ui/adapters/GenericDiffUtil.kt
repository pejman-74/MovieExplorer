package com.movie_explorer.ui.adapters

import androidx.recyclerview.widget.DiffUtil


class GenericDiffUtil<T> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem

    override fun areContentsTheSame(oldItem: T, newItem: T) =
        oldItem.toString() == newItem.toString()

}