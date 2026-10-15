package com.georgiytitov.movieexplorer.presentation.resources

import java.util.Locale

object Strings {
    const val MOVIES = "Movies"
    const val FAVORITES = "Favorites"
    const val MOVIE_DETAILS = "Movie details"
    const val NOW_PLAYING = "Now playing"
    const val UPCOMING = "Upcoming"
    const val TOP_RATED = "Top rated"
    const val BACK_TO_MOVIES = "Back to movies"
    const val REFRESH_MOVIES = "Refresh movies"
    const val NO_MOVIES = "No movies yet"
    const val MOVIES_UNAVAILABLE = "Movies unavailable"
    const val EMPTY_MOVIES = "Try another tab, or check again later."
    const val LOAD_MOVIES_ERROR = "Couldn’t load movies. Check your connection and try again."
    const val LOAD_MORE_ERROR = "Couldn’t load more"
    const val DETAILS_UNAVAILABLE = "Details unavailable"
    const val MOVIE_NOT_FOUND = "This movie could not be found."
    const val LOAD_DETAILS_ERROR =
        "Movie details are unavailable. Check your connection and try again."
    const val RELEASE_TBA = "Release TBA"
    const val RELEASE_DATE_TBA = "Release date TBA"
    const val OVERVIEW = "Overview"
    const val NO_DESCRIPTION = "No description available yet."
    const val DESCRIPTION_COMING_SOON = "Description coming soon."
    const val TMDB_ATTRIBUTION = "Movie data provided by TMDB"
    const val NO_POSTER = "No poster"
    const val POSTER_UNAVAILABLE = "Poster unavailable"
    const val UNTITLED = "Untitled"
    const val TRY_AGAIN = "Try again"
    const val ADD_TO_FAVORITES = "Add to favorites"
    const val SAVE_TO_FAVORITES = "Save to favorites"
    const val REMOVE_FROM_FAVORITES = "Remove from favorites"
    const val EMPTY_FAVORITES_TITLE = "Your favorites live here"
    const val EMPTY_FAVORITES_MESSAGE =
        "Tap the heart on a movie to save it. Your favorites stay available offline."
    const val READ_FAVORITES_ERROR = "Couldn’t read saved favorites."
    const val SAVE_FAVORITE_ERROR = "Couldn’t save this change. Please try again."
    const val OFFLINE_MESSAGE = "You’re offline · saved movies and favorites are available"
    const val DIRECT_TMDB_MESSAGE = "Backend unavailable · connected directly to TMDB"
    const val CACHED_MESSAGE = "Movie services unavailable · showing saved content"

    fun posterDescription(title: String) = "Poster for $title"

    fun rating(value: Double) = String.format(Locale.US, "★ %.1f", value)

    fun ratingOutOfTen(value: Double) = String.format(Locale.US, "★ %.1f / 10", value)

    fun movieMetadata(
        year: String,
        ratingValue: Double,
    ) = listOf(year.ifBlank { RELEASE_TBA }, rating(value = ratingValue)).joinToString("  ·  ")
}
