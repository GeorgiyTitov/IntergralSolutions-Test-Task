package com.georgiytitov.movies.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.georgiytitov.movies.movie.MovieDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbMovie(
    long id,
    String title,
    @JsonProperty("poster_path") String posterPath,
    String overview,
    @JsonProperty("release_date") String releaseDate,
    @JsonProperty("vote_average") double rating) {
  public MovieDto toDto() {
    return new MovieDto(
        id,
        title,
        posterPath,
        overview == null ? "" : overview,
        releaseDate == null ? "" : releaseDate,
        rating);
  }
}
