import com.georgiytitov.buildlogic.AppConfig
import groovy.json.JsonOutput
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

val localConfig = Properties().apply {
    rootProject
        .file(AppConfig.LOCAL_CONFIGURATION)
        .takeIf { it.exists() }
        ?.inputStream()
        ?.use(::load)
}

fun config(
    name: String,
    fallback: String = AppConfig.EMPTY_VALUE,
): String {
    return providers.environmentVariable(name).orNull ?: localConfig.getProperty(name, fallback)
}

fun quoted(value: String): String = JsonOutput.toJson(value)

val backendUrl = config(AppConfig.BACKEND_URL_KEY, AppConfig.DEFAULT_BACKEND_URL)
require(backendUrl.endsWith(AppConfig.URL_SEPARATOR)) { AppConfig.BACKEND_URL_ERROR }

android {
    namespace = AppConfig.APPLICATION_ID
    compileSdk = libs.versions.androidSdk.get().toInt()
    defaultConfig {
        applicationId = AppConfig.APPLICATION_ID
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.androidSdk.get().toInt()
        versionCode = libs.versions.appCode.get().toInt()
        versionName = libs.versions.appName.get()
        buildConfigField(
            AppConfig.STRING_TYPE,
            AppConfig.TMDB_READ_ACCESS_TOKEN,
            quoted(config(AppConfig.TMDB_READ_ACCESS_TOKEN)),
        )
        buildConfigField(
            AppConfig.STRING_TYPE,
            AppConfig.TMDB_API_KEY,
            quoted(config(AppConfig.TMDB_API_KEY)),
        )
        buildConfigField(AppConfig.STRING_TYPE, AppConfig.BACKEND_URL_KEY, quoted(backendUrl))
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
    }
    packaging { resources.excludes += AppConfig.PACKAGING_EXCLUSIONS }
}

ksp { arg(AppConfig.ROOM_SCHEMA_ARGUMENT, file(AppConfig.ROOM_SCHEMA_DIRECTORY).absolutePath) }

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.icons)
    implementation(libs.androidx.compose.animation)
    implementation(libs.lifecycle)
    implementation(libs.viewmodel)
    implementation(libs.navigation)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.okhttp)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    debugImplementation(libs.androidx.compose.tooling)
}
