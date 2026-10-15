# Movie Explorer

Android movie browser built with Jetpack Compose, plus a Spring Boot API and PostgreSQL.
Browse Now Playing, Upcoming and Top Rated, open details, and save favorites offline.

## Run the system

**Required:** Android Studio, Android SDK 37, JDK 21, Docker Desktop and a TMDB credential.

1. Copy the configuration template:
   ```sh
   cp .env.example .env
   ```
2. Set `TMDB_READ_ACCESS_TOKEN` or `TMDB_API_KEY` in `.env`. The read token is used when both are set.
3. Start the API and database:
   ```sh
   docker compose up -d --build
   ```
4. Wait for [health](http://localhost:8080/actuator/health) to report `UP`.
5. Open `mobile/` in Android Studio, select JDK 21, sync, and run `app` on an emulator.
   On Android 17, allow local-network access when prompted.

| Tool | Address / file |
| --- | --- |
| Swagger UI | [localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) |
| OpenAPI | [localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) |
| Postman collection | [backend/postman/MovieExplorer.postman_collection.json](backend/postman/MovieExplorer.postman_collection.json) |

The emulator uses `http://10.0.2.2:8080/`. For a USB device, run `adb reverse tcp:8080 tcp:8080`,
set `BACKEND_URL=http://127.0.0.1:8080/` in `.env`, and rebuild the app.
`BACKEND_URL` must end with `/`; release builds require HTTPS.

## Configuration and behavior

- Android reads `.env` or environment variables at **build time**; rebuild after changing them.
  Spring reads TMDB credentials at **runtime**; recreate the backend after changing them.
  `.env` is excluded from Git.
- Movie requests use the backend, then direct TMDB, then saved Room data.
- Online startup prepares three pages per tab and their posters. Further requested pages are saved too.
  Metadata stays in Room; posters use a 100 MiB disk cache with 24-hour online freshness.
- Favorites update locally first and sync on launch, reconnection, a change, or manual retry.
  Failures show a short snackbar; retry shows progress and its result.
- `docker compose down` stops the services and preserves the PostgreSQL volume.

See [mobile architecture and limitations](mobile/README.md) and [backend API](backend/README.md).
Optional KMP, trailers, benchmarks and automated tests are outside the current scope.
