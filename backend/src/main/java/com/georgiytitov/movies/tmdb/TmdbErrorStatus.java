package com.georgiytitov.movies.tmdb;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

enum TmdbErrorStatus {
    NOT_CONFIGURED(SERVICE_UNAVAILABLE, "TMDB credentials are not configured"),
    MOVIE_NOT_FOUND(NOT_FOUND, "Movie not found"),
    UNAVAILABLE(BAD_GATEWAY, "TMDB is temporarily unavailable"),
    UNREACHABLE(BAD_GATEWAY, "Could not reach TMDB"),
    INVALID_RESPONSE(BAD_GATEWAY, "Invalid TMDB response");

    private final HttpStatus status;
    private final String message;

    TmdbErrorStatus(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    boolean matches(HttpStatusCode responseStatus) {
        return status.isSameCodeAs(responseStatus);
    }

    ResponseStatusException exception() {
        return new ResponseStatusException(status, message);
    }
}
