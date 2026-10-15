package com.georgiytitov.movieexplorer.data.local

import android.content.Context
import androidx.core.content.edit
import java.util.UUID

internal class InstallationId(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    val value: String =
        preferences.getString(USER_ID_KEY, null) ?: UUID.randomUUID().toString().also { userId ->
            preferences.edit { putString(USER_ID_KEY, userId) }
        }

    private companion object {
        const val PREFERENCES_NAME = "installation"
        const val USER_ID_KEY = "user_id"
    }
}
