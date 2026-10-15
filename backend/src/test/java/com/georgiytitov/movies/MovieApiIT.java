package com.georgiytitov.movies;

import static com.georgiytitov.movies.support.TestMovies.FIRST;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.georgiytitov.movies.favorite.FavoriteRepository;
import com.georgiytitov.movies.movie.MoviePageDto;
import com.georgiytitov.movies.movie.MovieTab;
import com.georgiytitov.movies.support.PostgresTestConfiguration;
import com.georgiytitov.movies.tmdb.TmdbClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = {"tmdb.token=", "tmdb.api-key="})
@Import(PostgresTestConfiguration.class)
class MovieApiIT {
    private static final String EMPTY_BODY = "";
    private static final String ADD_FIRST_MOVIE = "{\"movieId\":1}";
    private static final JsonMapper JSON = JsonMapper.builder().build();

    @LocalServerPort
    private int port;
    @MockitoBean
    private TmdbClient tmdb;
    @Autowired
    private JdbcClient jdbc;

    private final HttpClient client = HttpClient.newBuilder(
        ).connectTimeout(Duration.ofSeconds(5)).build(
    );
    private UUID user;

    @BeforeEach
    void setUp() {
        user = UUID.randomUUID();
    }

    @ParameterizedTest
    @EnumSource(MovieTab.class)
    void everyTabSupportsPagination(MovieTab tab) throws Exception {
        when(tmdb.movies(tab, 2)).thenReturn(new MoviePageDto(2, 3, List.of(FIRST)));
        var response = request("GET", "/movies?tab=" + tab.name() + "&page=2", EMPTY_BODY);
        assertEquals(200, response.statusCode());
        assertEquals(2, body(response).get("page").asInt());
        assertEquals(FIRST.title(), body(response).get("movies").get(0).get("title").asText());
        verify(tmdb).movies(tab, 2);
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "/movies?page=0",
            "/movies?page=501",
            "/movies?page=abc",
            "/movies?tab=unknown",
            "/movies/-1",
            "/movies/not-an-id",
            "/users/not-a-uuid/favorites"
        })
    void invalidRequestsReturn400WithoutCallingTmdb(String path) throws Exception {
        assertEquals(400, request("GET", path, EMPTY_BODY).statusCode());
        verifyNoInteractions(tmdb);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"movieId\":0}", "{\"movieId\":-1}", "not-json"})
    void invalidFavoriteBodiesReturn400(String content) throws Exception {
        assertEquals(400, request("POST", favoritesPath(user), content).statusCode());
        verifyNoInteractions(tmdb);
    }

    @Test
    void unknownMovieReturns404() throws Exception {
        when(tmdb.movie(FIRST.id())).thenThrow(new ResponseStatusException(NOT_FOUND));
        assertEquals(404, request("GET", "/movies/" + FIRST.id(), EMPTY_BODY).statusCode());
    }

    @Test
    void upstreamFailureReturns502() throws Exception {
        when(tmdb.movie(FIRST.id())).thenThrow(new ResponseStatusException(BAD_GATEWAY));
        assertEquals(502, request("GET", "/movies/" + FIRST.id(), EMPTY_BODY).statusCode());
    }

    @Test
    void favoriteSnapshotsPersistInPostgresAndRemainIsolatedByUser() throws Exception {
        when(tmdb.movie(FIRST.id())).thenReturn(FIRST);
        String path = favoritesPath(user);
        var created = request("POST", path, ADD_FIRST_MOVIE);
        assertEquals(201, created.statusCode());
        assertEquals(FIRST.id(), body(created).get("id").asLong());

        var reloadedRepository = new FavoriteRepository(jdbc);
        assertEquals(List.of(FIRST), reloadedRepository.findAll(user));
        when(tmdb.movie(FIRST.id())).thenThrow(new ResponseStatusException(BAD_GATEWAY));
        var saved = request("GET", path, EMPTY_BODY);
        assertEquals(200, saved.statusCode());
        assertEquals(FIRST.title(), body(saved).get(0).get("title").asText());
        assertEquals(0, body(request("GET", favoritesPath(UUID.randomUUID()), EMPTY_BODY)).size());

        assertEquals(204, request("DELETE", path + "/" + FIRST.id(), EMPTY_BODY).statusCode());
        assertEquals(0, body(request("GET", path, EMPTY_BODY)).size());
        assertEquals(404, request("DELETE", path + "/" + FIRST.id(), EMPTY_BODY).statusCode());
    }

    @Test
    void duplicateFavoriteDoesNotCreateAnotherDatabaseRow() throws Exception {
        when(tmdb.movie(FIRST.id())).thenReturn(FIRST);
        String path = favoritesPath(user);
        assertEquals(201, request("POST", path, ADD_FIRST_MOVIE).statusCode());
        assertEquals(400, request("POST", path, ADD_FIRST_MOVIE).statusCode());
        assertEquals(1, new FavoriteRepository(jdbc).findAll(user).size());
    }

    @Test
    void detailsExposeAllRequiredFields() throws Exception {
        when(tmdb.movie(FIRST.id())).thenReturn(FIRST);
        var response = request("GET", "/movies/" + FIRST.id(), EMPTY_BODY);
        assertEquals(200, response.statusCode());
        JsonNode movie = body(response);
        assertEquals(FIRST.posterPath(), movie.get("posterPath").asText());
        assertEquals(FIRST.overview(), movie.get("overview").asText());
        assertEquals(FIRST.releaseDate(), movie.get("releaseDate").asText());
        assertEquals(FIRST.rating(), movie.get("rating").asDouble());
    }

    private String favoritesPath(UUID userId) {
        return "/users/" + userId + "/favorites";
    }

    private JsonNode body(HttpResponse<String> response) {
        return JSON.readTree(response.body());
    }

    private HttpResponse<String> request(String method, String path, String content)
        throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
            .timeout(Duration.ofSeconds(10))
            .header("Content-Type", "application/json")
            .method(method, HttpRequest.BodyPublishers.ofString(content))
            .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
