package com.movie_explorer.ui.fragments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.movie_explorer.data.repository.FakeRepository
import com.movie_explorer.data.repository.RepositoryInterface
import com.movie_explorer.di.RepositoryModule
import com.movie_explorer.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
@UninstallModules(RepositoryModule::class)
@HiltAndroidTest
class MovieFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @BindValue
    @JvmField
    var repository: RepositoryInterface = FakeRepository()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun test1() {
        launchFragmentInHiltContainer<MovieFragment> { }
    }
}