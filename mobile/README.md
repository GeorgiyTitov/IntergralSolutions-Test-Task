# Android app

Jetpack Compose · MVI · Clean Architecture · Retrofit · Room · Coil.
SDK 37, minimum Android 8 / API 26. Gradle runs with JDK 21.

## Build and run

Configure credentials and start Docker as described in the [root README](../README.md).
Open this folder in Android Studio, sync, then run `app`. From this directory:

```sh
./gradlew :app:assembleDebug :app:installDebug
```

`gradle/libversions.toml` contains library coordinates, plugin/library versions, SDK levels,
JVM bytecode target and app versions. Gradle Wrapper and daemon JVM settings use their own files. `buildSrc` contains shared build constants.

## Architecture and decisions

| Area | Responsibility / reason |
| --- | --- |
| `domain` | Framework-independent models and repository contracts. |
| `data` | Retrofit sources, DTO mapping, Room, connection monitoring and poster caching. |
| `presentation` | Immutable state, events, view models, Compose screens and navigation. |
| `di` | Application-scoped dependencies, constructed explicitly in `AppGraph`. |

One Android app module keeps setup small; packages enforce the layer boundaries.
Screens send events to `handleEvent`, view models update `StateFlow`, and Compose renders state.
Each `ScreenView` owns view-model creation, lifecycle-aware collection and navigation effects.
The view model owns pagination; the list requests another page near its end.
Retrofit interfaces and the `MovieDataSource` contract keep remote sources replaceable.

## Offline and favorites

- Requests try **backend → direct TMDB → Room**. Status banners explain offline/fallback modes.
- Online launch and reconnection prepare **three pages per tab** in the background.
  Requested pages and details are saved; refreshing one page preserves the others.
  Room data has no expiry and survives restarts.
- Every loaded page also downloads its posters into the shared **100 MiB HTTP cache**.
  Images are fresh for 24 hours online; offline reads can use older cached images.
  Downloads need time to finish, and cached image files can be evicted.
- Favorites use Room as the local source of truth. Changes appear immediately, then the app
  compares local/server IDs and sends the difference. Sync runs on launch, reconnection,
  local changes or manual retry. A mutex serializes these reconciliations.
- Errors appear in short snackbars. Retrying shows “Syncing favorites…” and then a result.
  Pull to refresh and the toolbar button also show progress and a result.

## Limitations and next steps

Favorites belong to an installation UUID; synchronization makes the server match the local set.
There is no account login, multi-device merge or background WorkManager job.
Offline access covers prepared/loaded content. UI text is English; optional tests, KMP,
trailers and benchmarks are not implemented.

With more time: test view models/repositories, expand accessibility/device coverage, and add
account-based synchronization. TMDB credentials come from build configuration and are embedded
in the APK; a production release needs a credential distribution strategy.
