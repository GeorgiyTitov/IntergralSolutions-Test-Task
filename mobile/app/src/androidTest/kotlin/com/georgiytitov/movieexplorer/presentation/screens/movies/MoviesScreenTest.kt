package com.georgiytitov.movieexplorer.presentation.screens.movies

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.domain.model.Movie
import com.georgiytitov.movieexplorer.domain.model.MovieTab.UPCOMING
import com.georgiytitov.movieexplorer.presentation.screens.movies.compose.MoviesScreen
import com.georgiytitov.movieexplorer.presentation.screens.movies.mvi.MoviesEvent
import com.georgiytitov.movieexplorer.presentation.screens.movies.mvi.MoviesState
import com.georgiytitov.movieexplorer.presentation.theme.MovieTheme
import com.georgiytitov.movieexplorer.support.UiTestMovies
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MoviesScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun selectingTabSendsItsEventAndUpdatesSelection() {
        var state by mutableStateOf(MoviesState(loading = false, totalPages = 1))
        val events = mutableListOf<MoviesEvent>()
        compose.setContent(composable = {
            MovieTheme(
                content = {
                    MoviesScreen(
                        state = state,
                        handleEvent = { event ->
                            events += event
                            if (event is MoviesEvent.SelectTab) {
                                state = state.copy(tab = event.tab)
                            }
                        },
                        favoriteIds = emptySet(),
                        toggleFavorite = {},
                        openMovie = {},
                    )
                },
            )
        })
        compose.onNodeWithText("Upcoming").performClick().assertIsSelected()
        compose.runOnIdle { assertTrue(events.contains(MoviesEvent.SelectTab(UPCOMING))) }
    }

    @Test
    fun favoriteButtonUpdatesWithoutOpeningTheMovie() {
        var favoriteIds by mutableStateOf(emptySet<Long>())
        var openedMovie: Long? = null
        var toggledMovie: Movie? = null
        compose.setContent(composable = {
            MovieTheme(
                content = {
                    MoviesScreen(
                        state = MoviesState(
                            movies = listOf(UiTestMovies.first),
                            loading = false,
                            totalPages = 1,
                        ),
                        handleEvent = {},
                        favoriteIds = favoriteIds,
                        toggleFavorite = { movie ->
                            toggledMovie = movie
                            favoriteIds = setOf(movie.id)
                        },
                        openMovie = { id -> openedMovie = id },
                    )
                },
            )
        })
        compose.onNodeWithContentDescription("Add to favorites").performClick()
        compose.onNodeWithContentDescription("Remove from favorites").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(UiTestMovies.first, toggledMovie)
            assertNull(openedMovie)
        }
        compose.onNodeWithText(UiTestMovies.first.title).performClick()
        compose.runOnIdle { assertEquals(UiTestMovies.first.id, openedMovie) }
    }

    @Test
    fun errorStateOffersAWorkingRetryAction() {
        val events = mutableListOf<MoviesEvent>()
        compose.setContent(composable = {
            MovieTheme(
                content = {
                    MoviesScreen(
                        state = MoviesState(loading = false, error = R.string.load_movies_error),
                        handleEvent = events::add,
                        favoriteIds = emptySet(),
                        toggleFavorite = {},
                        openMovie = {},
                    )
                },
            )
        })
        compose.onNodeWithText("Movies unavailable").assertIsDisplayed()
        compose.onNodeWithText("Try again").performClick()
        compose.runOnIdle { assertTrue(events.contains(MoviesEvent.Refresh)) }
    }

    @Test
    fun scrollingNearTheEndRequestsTheNextPage() {
        val events = mutableListOf<MoviesEvent>()
        val movies = (1..20).map { id ->
            UiTestMovies.first.copy(id = id.toLong(), title = "Movie $id")
        }
        compose.setContent(composable = {
            MovieTheme(
                content = {
                    MoviesScreen(
                        state = MoviesState(movies = movies, loading = false, totalPages = 2),
                        handleEvent = events::add,
                        favoriteIds = emptySet(),
                        toggleFavorite = {},
                        openMovie = {},
                    )
                },
            )
        })
        compose.runOnIdle { assertTrue(events.isEmpty()) }
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("Movie 20"))
        compose.runOnIdle { assertTrue(events.contains(MoviesEvent.LoadNext)) }
    }
}
