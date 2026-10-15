package com.georgiytitov.movieexplorer.presentation.screens.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.georgiytitov.movieexplorer.R
import com.georgiytitov.movieexplorer.presentation.screens.details.compose.DetailsScreen
import com.georgiytitov.movieexplorer.presentation.screens.details.mvi.DetailsState
import com.georgiytitov.movieexplorer.presentation.theme.MovieTheme
import com.georgiytitov.movieexplorer.support.UiTestMovies
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailsScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun loadedDetailsShowTitleDescriptionAndReleaseYear() {
        compose.setContent(composable = {
            MovieTheme(
                content = {
                    DetailsScreen(
                        state = DetailsState(movie = UiTestMovies.first, loading = false),
                        refresh = {},
                    )
                },
            )
        })
        compose.onNodeWithText(UiTestMovies.first.title).assertIsDisplayed()
        compose.onNodeWithText("2026").assertIsDisplayed()
        compose.onNodeWithText(UiTestMovies.first.overview).assertIsDisplayed()
    }

    @Test
    fun unavailableDetailsOfferAWorkingRetryAction() {
        var retried = false
        compose.setContent(composable = {
            MovieTheme(
                content = {
                    DetailsScreen(
                        state = DetailsState(loading = false, error = R.string.load_details_error),
                        refresh = { retried = true },
                    )
                },
            )
        })
        compose.onNodeWithText("Try again").performClick()
        compose.runOnIdle { assertTrue(retried) }
    }
}
