package com.georgiytitov.movies.tmdb;

import com.georgiytitov.movies.movie.MovieDto;
import com.georgiytitov.movies.movie.MoviePageDto;
import com.georgiytitov.movies.movie.MoviePagination;
import com.georgiytitov.movies.movie.MovieTab;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TmdbClient {
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String MOVIE_PATH = "/movie/";
    private static final String LANGUAGE_PARAMETER = "language";
    private static final String DEFAULT_LANGUAGE = "en-US";
    private static final String PAGE_PARAMETER = "page";
    private static final String API_KEY_PARAMETER = "api_key";
    private static final int CONNECT_TIMEOUT_SECONDS = 3;
    private static final int READ_TIMEOUT_SECONDS = 8;
    private static final String MISSING_CREDENTIALS = "TMDB credentials are not configured";
    private static final String MOVIE_NOT_FOUND = "Movie not found";
    private static final String TMDB_UNAVAILABLE = "TMDB is temporarily unavailable";
    private static final String TMDB_UNREACHABLE = "Could not reach TMDB";
    private static final String INVALID_RESPONSE = "Invalid TMDB response";

    private final RestClient client;
    private final String token;
    private final String apiKey;

    public TmdbClient(
        RestClient.Builder builder,
        @Value("${tmdb.token}") String token,
        @Value("${tmdb.api-key}") String apiKey
    ) {
        this.token = token;
        this.apiKey = apiKey;
        var factory = new JdkClientHttpRequestFactory(
            HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .build());
        factory.setReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS));
        client = builder
            .baseUrl(BASE_URL)
            .requestFactory(factory)
            .defaultHeaders(
                headers -> {
                    if (!token.isBlank()) {
                        headers.setBearerAuth(token);
                    }
                })
            .build();
    }

    private void validateConfiguration() {
        if (token.isBlank() && apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, MISSING_CREDENTIALS);
        }
    }

    private void handleError(HttpRequest request, ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MOVIE_NOT_FOUND);
        }
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, TMDB_UNAVAILABLE);
    }

    public MoviePageDto movies(MovieTab tab, int page) {
        validateConfiguration();
        try {
            TmdbPage response = client
                .get()
                .uri(
                    builder -> {
                        builder
                            .path(MOVIE_PATH + tab.name())
                            .queryParam(LANGUAGE_PARAMETER, DEFAULT_LANGUAGE)
                            .queryParam(PAGE_PARAMETER, page);
                        if (token.isBlank()) {
                            builder.queryParam(API_KEY_PARAMETER, apiKey);
                        }
                        return builder.build();
                    })
                .retrieve()
                .onStatus(status -> status.isError(), this::handleError)
                .body(TmdbPage.class);
            if (response == null || response.results() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, INVALID_RESPONSE);
            }
            return new MoviePageDto(
                response.page(),
                Math.min(response.totalPages(), MoviePagination.MAX_PAGE),
                response.results().stream().map(TmdbMovie::toDto).toList()
            );
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, TMDB_UNREACHABLE);
        }
    }

    public MovieDto movie(long id) {
        validateConfiguration();
        try {
            TmdbMovie response = client
                .get()
                .uri(
                    builder -> {
                        builder.path(MOVIE_PATH + id)
                            .queryParam(LANGUAGE_PARAMETER, DEFAULT_LANGUAGE);
                        if (token.isBlank()) {
                            builder.queryParam(API_KEY_PARAMETER, apiKey);
                        }
                        return builder.build();
                    })
                .retrieve()
                .onStatus(status -> status.isError(), this::handleError)
                .body(TmdbMovie.class);
            if (response == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, INVALID_RESPONSE);
            }
            return response.toDto();
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, TMDB_UNREACHABLE);
        }
    }
}
