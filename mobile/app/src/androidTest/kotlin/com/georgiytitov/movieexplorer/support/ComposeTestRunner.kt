package com.georgiytitov.movieexplorer.support

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class ComposeTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        classLoader: ClassLoader,
        className: String,
        context: Context,
    ): Application {
        return super.newApplication(classLoader, Application::class.java.name, context)
    }
}
