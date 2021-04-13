package com.movie_explorer.ui.fragments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.movie_explorer.R
import com.movie_explorer.data.repository.FakeRepository
import com.movie_explorer.data.repository.InternetStatus
import com.movie_explorer.data.repository.RepositoryInterface
import com.movie_explorer.di.RepositoryModule
import com.movie_explorer.ui.MainActivity
import com.movie_explorer.waitFor
import com.todkars.shimmer.ShimmerRecyclerView
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.android.synthetic.main.fragment_movie.*
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
@UninstallModules(RepositoryModule::class)
@HiltAndroidTest
class MovieFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val repository: RepositoryInterface = FakeRepository()
    private val fakeRepository: FakeRepository get() = repository as FakeRepository

    private val firstMovie = fakeRepository.dummyMovieApisResponse.movies[0]
    private val firstMovieDetail = fakeRepository.dummyGetMovieDetailApiResponse

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun displayMovies_whenInternetHasLatency() {
        fakeRepository.setInternetLatency(1000)

        var rvm: ShimmerRecyclerView? = null
        launchActivity()?.onActivity {
            rvm = it.rv_movies
        }
        //check shimmer is showing
        assertThat(rvm?.isShimmerShowing).isTrue()

        onView(isRoot()).perform(waitFor(2000))

        //check shimmer is gone after internet latency

        assertThat(rvm?.isShimmerShowing).isFalse()

        //check showing data
        onView(ViewMatchers.withSubstring(firstMovie.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun displayFailingView_whenInternetIsOFF() {
        fakeRepository.setInternetStatus(InternetStatus.OFF)
        launchActivity()
        onView(ViewMatchers.withId(R.id.failing_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickRetryButtonInFailingView_whenInternetBackToON() {
        fakeRepository.setInternetStatus(InternetStatus.OFF)
        launchActivity()
        onView(ViewMatchers.withId(R.id.failing_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        fakeRepository.setInternetStatus(InternetStatus.ON)

        onView(ViewMatchers.withId(R.id.btn_retry))
            .perform(click())

        onView(ViewMatchers.withId(R.id.failing_view))
            .check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        onView(ViewMatchers.withSubstring(firstMovie.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun navigateToDetailMovie_whenClickOnMovieItem() {
        launchActivity()
        onView(ViewMatchers.withSubstring(firstMovie.title))
            .perform(click())

        onView(ViewMatchers.withText(firstMovieDetail.released)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )
    }


    private fun launchActivity(): ActivityScenario<MainActivity>? {
        val activityScenario = launch(MainActivity::class.java)
        activityScenario.onActivity { activity ->
            // Disable animations in RecyclerView
            activity.rv_movies.itemAnimator = null
        }
        return activityScenario
    }
}