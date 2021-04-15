package com.movie_explorer

import com.movie_explorer.ui.fragments.DetailFragmentTest
import com.movie_explorer.ui.fragments.FavoriteFragmentTest
import com.movie_explorer.ui.fragments.MovieFragmentTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(
    MovieFragmentTest::class,
    DetailFragmentTest::class,
    FavoriteFragmentTest::class
)
class FragmentsTestSuite
