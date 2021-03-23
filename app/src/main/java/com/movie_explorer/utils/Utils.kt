package com.movie_explorer.utils

fun List<String>.toGenresStyleText(): String {
    var genres = ""
    forEachIndexed { index, s ->
        genres += s
        if (index < size - 1)
            genres += ", "
    }
    return genres
}