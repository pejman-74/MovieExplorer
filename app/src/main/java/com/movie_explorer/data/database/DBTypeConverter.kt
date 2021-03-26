package com.movie_explorer.data.database

import androidx.room.TypeConverter

class DBTypeConverter {

    @TypeConverter
    fun stringListToString(strings: List<String>?): String? =
        strings?.joinToString(",")

    @TypeConverter
    fun stringToListString(string: String?): List<String>? {
        return string?.split(",")
    }
}