package com.movie_explorer.ui.fragments

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.movie_explorer.R
import com.movie_explorer.data.model.FavoriteMovie
import com.movie_explorer.data.repository.AndroidFakeRepository
import com.movie_explorer.data.repository.RepositoryInterface
import com.movie_explorer.di.RepositoryModule
import com.movie_explorer.launchFragmentInHiltContainer
import com.movie_explorer.utils.dummyMovieApisResponse
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
    val repository: RepositoryInterface = AndroidFakeRepository()
    private val androidFakeRepository: AndroidFakeRepository get() = repository as AndroidFakeRepository

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val firstMovie = dummyMovieApisResponse.movies[0]


    @Before
    fun setUp() {
        hiltRule.inject()
    }

    //fill movie db and insert one favoriteMovie
    private fun setMovieAndFavoriteMovie() = runBlockingTest {
        androidFakeRepository.saveMovie(androidFakeRepository.searchMovieApi().movies)
        androidFakeRepository.saveFavoriteMovie(FavoriteMovie(1, ""))
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

    @Test
    fun testSwipeItem_ItemDelete() = runBlockingTest {

        setMovieAndFavoriteMovie()

        launchFragmentInHiltContainer<FavoriteFragment>()

        assertThat(androidFakeRepository.favoriteMovies().first().size).isEqualTo(1)

        onView(ViewMatchers.withText(firstMovie.title)).perform(swipeLeft())

        assertThat(androidFakeRepository.favoriteMovies().first().size).isEqualTo(0)

    }

    @Test
    fun testSwipeItem_ItemUndo() = runBlockingTest {

        setMovieAndFavoriteMovie()

        launchFragmentInHiltContainer<FavoriteFragment>()

        assertThat(androidFakeRepository.favoriteMovies().first().size).isEqualTo(1)

        onView(withText(firstMovie.title)).perform(swipeLeft())

        assertThat(androidFakeRepository.favoriteMovies().first().size).isEqualTo(0)

        //check snack bar
        onView(withText(context.getString(R.string.deleted))).check(matches(isDisplayed()))

        //click snack bar undo button
        onView(withText(context.getString(R.string.undo))).perform(click())

        assertThat(androidFakeRepository.favoriteMovies().first().size).isEqualTo(1)
    }
}