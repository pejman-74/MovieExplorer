package com.movie_explorer.ui.fragments

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.movie_explorer.R
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.data.repository.FakeRepository
import com.movie_explorer.data.repository.RepositoryInterface
import com.movie_explorer.di.RepositoryModule
import com.movie_explorer.launchFragmentInHiltContainer
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
@MediumTest
@UninstallModules(RepositoryModule::class)
@HiltAndroidTest
class FavoriteFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val repository: RepositoryInterface = FakeRepository()
    private val fakeRepository: FakeRepository get() = repository as FakeRepository

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val firstMovie = fakeRepository.dummyMovieApisResponse.movies[0]

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    //fill movie db and insert one favoriteMovie
    private fun setMovieAndFavoriteMovie() = runBlockingTest {
        fakeRepository.saveMovie(fakeRepository.searchMovieApi().movies)
        fakeRepository.saveFavoriteMovie(FavoriteMovie(1, ""))
    }

    @Test
    fun lunchFavoriteFragment_WithEmptyDB() {

        launchFragmentInHiltContainer<FavoriteFragment>()
        onView(ViewMatchers.withId(R.id.im_bookmark)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    @Test
    fun lunchFavoriteFragment_whenHasFavoriteMovieExist() = runBlockingTest {

        setMovieAndFavoriteMovie()

        launchFragmentInHiltContainer<FavoriteFragment>()


        onView(ViewMatchers.withId(R.id.im_bookmark)).check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))

        onView(ViewMatchers.withSubstring(firstMovie.title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun deleteFavoriteMovieWithLongTouch() = runBlockingTest {

        setMovieAndFavoriteMovie()

        launchFragmentInHiltContainer<FavoriteFragment>()

        //long click on it
        onView(ViewMatchers.withSubstring(firstMovie.title)).perform(longClick())


        //click on delete icon in action bar
        onView(ViewMatchers.withId(R.id.delete_favorite_items)).perform(click())

        //alert dialog is showing
        onView(ViewMatchers.withText(context.getString(R.string.delete_favorite_dialog_title))).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )

        //click on positive button to accept deleting
        onView(
            ViewMatchers.withText(context.getString(R.string.delete_favorite_dialog_positive_button))
        ).perform(click())


        //item dos'nt exist after delete
        onView(ViewMatchers.withSubstring(firstMovie.title)).check(doesNotExist())
    }

    @Test
    fun navigateToDetailFragmentOnItemClicked() = runBlockingTest {

        setMovieAndFavoriteMovie()

        val navController = mock(NavController::class.java)

        launchFragmentInHiltContainer<FavoriteFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(ViewMatchers.withSubstring(firstMovie.title)).perform(click())

        verify(navController).navigate(DetailFragmentDirections.toDetailFragment(1))
    }
}