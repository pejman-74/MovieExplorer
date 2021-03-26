package com.movie_explorer.data.database

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DBTypeConverterTest {

    @Test
    fun stringListToString_Test() {
        val string = DBTypeConverter().stringListToString(listOf("A", "B", "C"))
        assertThat(string).isEqualTo("A,B,C")
    }

    @Test
    fun stringToListString_Test() {
        val string = "A, B, C"
        val list = DBTypeConverter().stringToListString(string)
        assertThat(list?.size).isEqualTo(3)
    }
}