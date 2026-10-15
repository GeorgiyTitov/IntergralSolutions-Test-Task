package com.georgiytitov.movies.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.georgiytitov.movies.movie.MoviePageDto;
import com.georgiytitov.movies.movie.MoviePagination;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
record TmdbPage(
    int page,
    @JsonProperty("total_pages") int totalPages,
    List<TmdbMovie> results
) {
    MoviePageDto toDto() {
        return new MoviePageDto(
            page,
            Math.min(totalPages, MoviePagination.MAX_PAGE),
            results.stream().map(TmdbMovie::toDto).toList()
        );
    }
}
