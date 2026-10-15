package com.georgiytitov.movieexplorer.data.local

import android.content.Context
import java.util.UUID

class InstallationId(
    context: Context,
) {
    private val preferences = context.getSharedPreferences("installation", Context.MODE_PRIVATE)
    val value: String =
        preferences.getString("user_id", null) ?: UUID.randomUUID().toString().also {
            preferences.edit().putString("user_id", it).apply()
        }
}
