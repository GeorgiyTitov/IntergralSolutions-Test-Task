# Movie API

Java 21 / Spring Boot. Proxies TMDB and stores per-user favorite snapshots in PostgreSQL.

## Run

From the repository root, copy `.env.example` to `.env`, set `TMDB_READ_ACCESS_TOKEN`
or `TMDB_API_KEY`, then run:

```sh
docker compose up -d --build
```

| Endpoint | URL |
| --- | --- |
| Health | [localhost:8080/actuator/health](http://localhost:8080/actuator/health) |
| Swagger UI | [localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) |
| OpenAPI | [localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) |

Compose builds the service with the Maven wrapper and starts PostgreSQL with a named volume.
The API uses port 8080; the database has no published host port.
Use `docker compose logs backend` for logs. `docker compose down` preserves favorites.

TMDB credentials are runtime environment values; the read token is used when both are set.
`POSTGRES_USER` / `POSTGRES_PASSWORD` can override the Compose defaults and must match the
existing database volume. Compose supplies `DATABASE_URL`.

## API

| Method | Path | Success |
| --- | --- | --- |
| GET | `/movies?tab=now_playing&page=1` | 200, paginated movies |
| GET | `/movies/{id}` | 200, movie details |
| GET | `/users/{userId}/favorites` | 200, saved movies |
| POST | `/users/{userId}/favorites` with `{"movieId": 123}` | 201, saved movie |
| DELETE | `/users/{userId}/favorites/{movieId}` | 204 |

Tabs: `now_playing`, `upcoming`, `top_rated`. Pages: 1–500; movie IDs must be positive;
`userId` must be a UUID. Invalid input and duplicates return **400**; missing movies/favorites
return **404**. TMDB failures return **502**; missing credentials return **503**.

## Design and verification

Controllers handle HTTP mapping/validation; `TmdbClient` prepares requests and reads typed
movie/page responses. `TmdbConstants` holds configuration constants, and `TmdbErrorStatus`
holds HTTP error mappings. DTOs reshape TMDB responses.
`FavoriteService` coordinates operations; `FavoriteRepository` uses parameterized Spring JDBC.
The `(user_id, movie_id)` primary key separates users. Saved snapshots keep favorites readable
when TMDB is unavailable. `userId` identifies an installation; authentication is outside scope.

Import [the Postman collection](postman/MovieExplorer.postman_collection.json), set `baseUrl`
if needed, and run in order. It covers list/details, favorite creation/list/removal,
user isolation, invalid input and missing resources; a successful run cleans up its data.
Optional automated tests, TMDB response caching, 409/idempotent handling and API versioning
are left for a separate phase.
