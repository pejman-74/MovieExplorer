package com.movie_explorer.ui.fragments

import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.movie_explorer.R
import com.movie_explorer.data.repository.FakeRepository
import com.movie_explorer.data.repository.InternetStatus
import com.movie_explorer.data.repository.RepositoryInterface
import com.movie_explorer.di.RepositoryModule
import com.movie_explorer.launchFragmentInHiltContainer
import com.movie_explorer.waitFor
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
@HiltAndroidTest
@MediumTest
@UninstallModules(RepositoryModule::class)
class DetailFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    val repository: RepositoryInterface = FakeRepository()

    private val fakeRepository: FakeRepository get() = repository as FakeRepository

    private val firstMovieDetail = fakeRepository.dummyGetMovieDetailApiResponse

    @Before
    fun setUp() {
        hiltRule.inject()
    }


    @Test
    fun displayMovieDetail_WhenIntentIsOFF() {
        fakeRepository.setInternetStatus(InternetStatus.OFF)
        launchFragmentInHiltContainer<DetailFragment>(
            bundleOf("movieId" to firstMovieDetail.id)
        )
        onView(withId(R.id.im_dissatisfied)).check(matches(isDisplayed()))
    }

    @Test
    fun displayMovieDetail_WhenIntentHasLatency() = runBlockingTest {

        fakeRepository.setInternetLatency(500)

        launchFragmentInHiltContainer<DetailFragment>(
            bundleOf("movieId" to firstMovieDetail.id)
        )

        //check shimmer effect
        onView(withId(R.id.shimmerLayout)).check(matches(isDisplayed()))

        //waiting for elapse internet latency
        onView(isRoot()).perform(waitFor(1000))

        //check data sets after internet latency
        onView(withText(firstMovieDetail.released)).check(matches(isDisplayed()))

    }

    @Test
    fun makeMovieFavorite() = runBlockingTest {
        //save movies to db (for making relation in db)
        fakeRepository.saveMovie(fakeRepository.searchMovieApi().movies)

        launchFragmentInHiltContainer<DetailFragment>(
            bundleOf("movieId" to firstMovieDetail.id)
        )
        //click on favorite menu item
        onView(withId(R.id.favoriteMenuItem)).perform(click())

        //check added to favorite movies in repository
        assertThat(fakeRepository.favoriteMovies().first().size).isEqualTo(1)

    }
}
