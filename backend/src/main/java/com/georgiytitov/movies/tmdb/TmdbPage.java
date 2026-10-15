package com.georgiytitov.movies.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
record TmdbPage(
    int page,
    @JsonProperty("total_pages") int totalPages,
    List<TmdbMovie> results
) {
}
