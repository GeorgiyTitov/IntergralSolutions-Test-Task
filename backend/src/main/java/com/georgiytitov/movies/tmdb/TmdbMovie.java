package com.georgiytitov.movies.tmdb;

import static java.util.Objects.requireNonNullElse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.georgiytitov.movies.movie.MovieDto;

@JsonIgnoreProperties(ignoreUnknown = true)
record TmdbMovie(
    long id,
    String title,
    @JsonProperty("poster_path") String posterPath,
    String overview,
    @JsonProperty("release_date") String releaseDate,
    @JsonProperty("vote_average") double rating
) {
    MovieDto toDto() {
        return new MovieDto(
            id,
            title,
            posterPath,
            requireNonNullElse(overview, ""),
            requireNonNullElse(releaseDate, ""),
            rating
        );
    }
}
