package com.movie_explorer.ui.fragments

import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.movie_explorer.EspressoIdlingResource
import com.movie_explorer.R
import com.movie_explorer.data.repository.AndroidFakeRepository
import com.movie_explorer.data.repository.RepositoryInterface
import com.movie_explorer.di.RepositoryModule
import com.movie_explorer.launchFragmentInHiltContainer
import com.movie_explorer.utils.InternetStatus
import com.movie_explorer.utils.dummyGetMovieDetailApiResponse
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.android.synthetic.main.fragment_detail_place_holder.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
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
    val repository: RepositoryInterface = AndroidFakeRepository()

    private val androidFakeRepository: AndroidFakeRepository get() = repository as AndroidFakeRepository

    private val firstMovieDetail = dummyGetMovieDetailApiResponse

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
    fun displayMovieDetail_WhenIntentIsOFF() {
        androidFakeRepository.setInternetStatus(InternetStatus.OFF)
        launchFragmentInHiltContainer<DetailFragment>(
            bundleOf("movieId" to firstMovieDetail.id)
        )
        onView(withId(R.id.im_dissatisfied)).check(matches(isDisplayed()))
    }

    @Test
    fun displayMovieDetail_WhenIntentHasLatency() {

        androidFakeRepository.setInternetLatency(1000)

        launchFragmentInHiltContainer<DetailFragment>(
            bundleOf("movieId" to firstMovieDetail.id)
        ) {
            //check shimmer is showing
            assertThat(shimmerLayout.isShimmerVisible).isTrue()
        }

        //check data sets after internet latency
        onView(withText(firstMovieDetail.released)).check(matches(isDisplayed()))

    }

    @Test
    fun makeMovieFavorite() = runBlockingTest {
        //save movies to db (for making relation in db)
        androidFakeRepository.saveMovie(androidFakeRepository.searchMovieApi().movies)

        launchFragmentInHiltContainer<DetailFragment>(
            bundleOf("movieId" to firstMovieDetail.id)
        )
        //click on favorite menu item
        onView(withId(R.id.favoriteMenuItem)).perform(click())

        //check added to favorite movies in repository
        assertThat(androidFakeRepository.favoriteMovies().first().size).isEqualTo(1)

    }
}
