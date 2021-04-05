package com.movie_explorer.wrapper

sealed class RefreshType {
    object Force : RefreshType()
    object Normal : RefreshType()
    class Search(val query: String) : RefreshType()
}