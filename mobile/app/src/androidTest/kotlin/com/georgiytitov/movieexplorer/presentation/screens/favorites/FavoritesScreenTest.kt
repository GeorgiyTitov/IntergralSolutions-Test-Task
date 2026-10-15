package com.georgiytitov.movieexplorer.presentation.screens.favorites

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.georgiytitov.movieexplorer.presentation.components.layout.ScreenFrame
import com.georgiytitov.movieexplorer.presentation.resources.ConnectionStatus.OFFLINE
import com.georgiytitov.movieexplorer.presentation.screens.favorites.compose.FavoritesFeedback
import com.georgiytitov.movieexplorer.presentation.screens.favorites.compose.FavoritesScreen
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesState
import com.georgiytitov.movieexplorer.presentation.screens.favorites.mvi.FavoritesViewModel
import com.georgiytitov.movieexplorer.presentation.theme.MovieTheme
import com.georgiytitov.movieexplorer.support.OfflineFavoritesRepository
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesScreenTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var viewModel: FavoritesViewModel

    @After
    fun tearDown() {
        if (::viewModel.isInitialized) {
            viewModel.viewModelScope.cancel()
        }
    }

    @Test
    fun emptyFavoritesExplainWhatToDo() {
        compose.setContent(composable = {
            MovieTheme(
                content = {
                    FavoritesScreen(
                        state = FavoritesState(loading = false),
                        handleEvent = {},
                        openMovie = {},
                    )
                },
            )
        })
        compose.onNodeWithText("Your favorites live here").assertIsDisplayed()
    }

    @Test
    fun everyRetryShowsProgressThenAnotherShortError() {
        val repository = OfflineFavoritesRepository()
        compose.setContent(composable = {
            val model = remember(calculation = { FavoritesViewModel(repository) })
            viewModel = model
            val state by model.state.collectAsState()
            val snackbars = remember(calculation = ::SnackbarHostState)
            MovieTheme(
                content = {
                    ScreenFrame(
                        isFavorites = true,
                        connectionStatus = OFFLINE,
                        snackbars = snackbars,
                        feedback = {
                            FavoritesFeedback(
                                state = state,
                                handleEvent = model::handleEvent,
                                snackbars = snackbars,
                            )
                        },
                        content = {
                            FavoritesScreen(
                                state = state,
                                handleEvent = model::handleEvent,
                                openMovie = {},
                            )
                        },
                    )
                },
            )
        })
        compose.onNodeWithText(FAILURE_MESSAGE).assertIsDisplayed()
        repeat(2) { attempt ->
            compose.onNodeWithText("Try again").performClick()
            compose.waitUntil(timeoutMillis = TIMEOUT_MILLIS) {
                compose.onNodeWithText("Syncing favorites…").isDisplayed()
            }
            compose.onNodeWithText("Syncing favorites…").assertIsDisplayed()
            compose.runOnIdle { repository.completeAttempt() }
            compose.waitUntil(timeoutMillis = TIMEOUT_MILLIS) {
                repository.synchronizationCount == attempt + 1 &&
                    compose.onAllNodesWithText(FAILURE_MESSAGE).fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithText(FAILURE_MESSAGE).assertIsDisplayed()
        }
        compose.waitUntil(timeoutMillis = TIMEOUT_MILLIS) {
            compose.onAllNodesWithText(FAILURE_MESSAGE).fetchSemanticsNodes().isEmpty()
        }
    }

    private companion object {
        const val TIMEOUT_MILLIS = 10_000L
        const val FAILURE_MESSAGE =
            "Favorites saved on this device. Couldn’t sync with the server."
    }
}
