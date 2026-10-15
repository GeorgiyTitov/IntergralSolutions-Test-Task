package com.georgiytitov.movies.tmdb;

import com.georgiytitov.movies.movie.MovieDto;
import com.georgiytitov.movies.movie.MoviePageDto;
import com.georgiytitov.movies.movie.MovieTab;
import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TmdbClient {
  private final RestClient client;
  private final String token;
  private final String apiKey;

  public TmdbClient(
      RestClient.Builder builder,
      @Value("${tmdb.token}") String token,
      @Value("${tmdb.api-key}") String apiKey) {
    this.token = token;
    this.apiKey = apiKey;
    var factory =
        new JdkClientHttpRequestFactory(
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build());
    factory.setReadTimeout(Duration.ofSeconds(8));
    client =
        builder
            .baseUrl("https://api.themoviedb.org/3")
            .requestFactory(factory)
            .defaultHeaders(
                headers -> {
                  if (!token.isBlank()) headers.setBearerAuth(token);
                })
            .build();
  }

  private <T> T get(String path, Integer page, Class<T> type) {
    if (token.isBlank() && apiKey.isBlank())
      throw new ResponseStatusException(
          HttpStatus.SERVICE_UNAVAILABLE, "TMDB credentials are not configured");
    try {
      return client
          .get()
          .uri(
              builder -> {
                builder.path(path).queryParam("language", "en-US");
                if (page != null) builder.queryParam("page", page);
                if (token.isBlank()) builder.queryParam("api_key", apiKey);
                return builder.build();
              })
          .retrieve()
          .onStatus(
              status -> status.isError(),
              (request, response) -> {
                if (response.getStatusCode().value() == 404)
                  throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found");
                throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "TMDB is temporarily unavailable");
              })
          .body(type);
    } catch (RestClientException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not reach TMDB");
    }
  }

  public MoviePageDto movies(MovieTab tab, int page) {
    TmdbPage response = get("/movie/" + tab.name(), page, TmdbPage.class);
    if (response == null || response.results() == null)
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Invalid TMDB response");
    return new MoviePageDto(
        response.page(),
        Math.min(response.totalPages(), 500),
        response.results().stream().map(TmdbMovie::toDto).toList());
  }

  public MovieDto movie(long id) {
    TmdbMovie response = get("/movie/" + id, null, TmdbMovie.class);
    if (response == null)
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Invalid TMDB response");
    return response.toDto();
  }
}
