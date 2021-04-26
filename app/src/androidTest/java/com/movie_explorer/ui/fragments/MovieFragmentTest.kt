package com.movie_explorer.ui.fragments

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.movie_explorer.EspressoIdlingResource
import com.movie_explorer.R
import com.movie_explorer.data.repository.AndroidFakeRepository
import com.movie_explorer.data.repository.RepositoryInterface
import com.movie_explorer.di.RepositoryModule
import com.movie_explorer.launchFragmentInHiltContainer
import com.movie_explorer.utils.InternetStatus
import com.movie_explorer.utils.dummyMovieApisResponse
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@MediumTest
@ExperimentalCoroutinesApi
@UninstallModules(RepositoryModule::class)
@HiltAndroidTest
class MovieFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val repository: RepositoryInterface = AndroidFakeRepository()
    private val androidFakeRepository: AndroidFakeRepository get() = repository as AndroidFakeRepository

    private val firstMovie = dummyMovieApisResponse.movies[0]

    @Before
    fun setUp() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }


    @Test
    fun displayMovies_whenInternetHasLatency() {

        androidFakeRepository.setInternetLatency(1000)

        launchFragmentInHiltContainer<MovieFragment> {
            //check shimmer is showing
            assertThat(rv_movies.isShimmerShowing).isTrue()
        }


        //check showing data
        onView(withSubstring(firstMovie.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun displayFailingView_whenInternetIsOFF() {
        androidFakeRepository.setInternetStatus(InternetStatus.OFF)
        launchFragmentInHiltContainer<MovieFragment>()
        onView(ViewMatchers.withId(R.id.failing_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickRetryButtonInFailingView_whenInternetBackToON() {
        androidFakeRepository.setInternetStatus(InternetStatus.OFF)
        launchFragmentInHiltContainer<MovieFragment>()
        onView(ViewMatchers.withId(R.id.failing_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        androidFakeRepository.setInternetStatus(InternetStatus.ON)

        onView(ViewMatchers.withId(R.id.btn_retry))
            .perform(click())

        onView(ViewMatchers.withId(R.id.failing_view))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        onView(withSubstring(firstMovie.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun navigateToDetailMovie_whenClickOnMovieItem() {
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<MovieFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }
        onView(withSubstring(firstMovie.title))
            .perform(click())

        verify(navController).navigate(MovieFragmentDirections.toDetailFragment(firstMovie.id))
    }


}