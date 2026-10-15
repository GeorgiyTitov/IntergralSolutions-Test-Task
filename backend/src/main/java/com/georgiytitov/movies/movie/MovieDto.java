package com.georgiytitov.movies.movie;

public record MovieDto(
    long id, String title, String posterPath, String overview, String releaseDate, double rating) {}
