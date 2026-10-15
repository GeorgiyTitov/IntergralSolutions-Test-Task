package com.georgiytitov.movies.tmdb;

import static com.georgiytitov.movies.tmdb.TmdbConstants.API_KEY_PARAMETER;
import static com.georgiytitov.movies.tmdb.TmdbConstants.API_KEY_PROPERTY;
import static com.georgiytitov.movies.tmdb.TmdbConstants.BASE_URL;
import static com.georgiytitov.movies.tmdb.TmdbConstants.CONNECT_TIMEOUT_SECONDS;
import static com.georgiytitov.movies.tmdb.TmdbConstants.DEFAULT_LANGUAGE;
import static com.georgiytitov.movies.tmdb.TmdbConstants.LANGUAGE_PARAMETER;
import static com.georgiytitov.movies.tmdb.TmdbConstants.MOVIE_PATH;
import static com.georgiytitov.movies.tmdb.TmdbConstants.PAGE_PARAMETER;
import static com.georgiytitov.movies.tmdb.TmdbConstants.READ_TIMEOUT_SECONDS;
import static com.georgiytitov.movies.tmdb.TmdbConstants.TOKEN_PROPERTY;
import static com.georgiytitov.movies.tmdb.TmdbErrorStatus.INVALID_RESPONSE;
import static com.georgiytitov.movies.tmdb.TmdbErrorStatus.MOVIE_NOT_FOUND;
import static com.georgiytitov.movies.tmdb.TmdbErrorStatus.NOT_CONFIGURED;
import static com.georgiytitov.movies.tmdb.TmdbErrorStatus.UNAVAILABLE;
import static com.georgiytitov.movies.tmdb.TmdbErrorStatus.UNREACHABLE;

import com.georgiytitov.movies.movie.MovieDto;
import com.georgiytitov.movies.movie.MoviePageDto;
import com.georgiytitov.movies.movie.MovieTab;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;

@Service
public class TmdbClient {
    private final RestClient client;
    private final String token;
    private final String apiKey;

    public TmdbClient(
        RestClient.Builder builder,
        @Value(TOKEN_PROPERTY) String token,
        @Value(API_KEY_PROPERTY) String apiKey
    ) {
        this.token = token;
        this.apiKey = apiKey;
        var factory = new JdkClientHttpRequestFactory(
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .build());
        factory.setReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS));
        client = builder.baseUrl(BASE_URL).requestFactory(factory)
            .defaultHeaders(
                headers -> {
                    if (!token.isBlank()) {
                        headers.setBearerAuth(token);
                    }
                })
            .build();
    }

    public MoviePageDto movies(MovieTab tab, int page) {
        try {
            TmdbPage response = request(tab.name(), page).body(TmdbPage.class);
            if (response == null || response.results() == null) {
                throw INVALID_RESPONSE.exception();
            }
            return response.toDto();
        } catch (RestClientException ex) {
            throw UNREACHABLE.exception();
        }
    }

    public MovieDto movie(long id) {
        try {
            TmdbMovie response = request(Long.toString(id), null).body(TmdbMovie.class);
            if (response == null) {
                throw INVALID_RESPONSE.exception();
            }
            return response.toDto();
        } catch (RestClientException ex) {
            throw UNREACHABLE.exception();
        }
    }

    private RestClient.ResponseSpec request(String path, Integer page) {
        validateConfiguration();
        return client.get().uri(builder -> movieUri(builder, path, page)).retrieve()
            .onStatus(HttpStatusCode::isError, this::handleError);
    }

    private URI movieUri(UriBuilder builder, String path, Integer page) {
        builder.path(MOVIE_PATH).pathSegment(path).queryParam(LANGUAGE_PARAMETER, DEFAULT_LANGUAGE);
        if (page != null) {
            builder.queryParam(PAGE_PARAMETER, page);
        }
        if (token.isBlank()) {
            builder.queryParam(API_KEY_PARAMETER, apiKey);
        }
        return builder.build();
    }

    private void validateConfiguration() {
        if (token.isBlank() && apiKey.isBlank()) {
            throw NOT_CONFIGURED.exception();
        }
    }

    private void handleError(HttpRequest request, ClientHttpResponse response) throws IOException {
        if (MOVIE_NOT_FOUND.matches(response.getStatusCode())) {
            throw MOVIE_NOT_FOUND.exception();
        }
        throw UNAVAILABLE.exception();
    }
}
