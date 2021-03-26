package com.movie_explorer.utils

import android.content.Context
import android.widget.Toast

fun List<String>.toGenresStyleText(): String {
    var genres = ""
    forEachIndexed { index, s ->
        genres += s
        if (index < size - 1)
            genres += ", "
    }
    return genres
}
fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}