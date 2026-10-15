package com.georgiytitov.movies.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.web.server.ResponseStatusException;

class MovieTabTest {
    @ParameterizedTest
    @EnumSource(MovieTab.class)
    void supportedTabsKeepTheirApiNames(MovieTab tab) {
        assertEquals(tab, MovieTab.parse(tab.name()));
    }

    @Test
    void unsupportedTabReturnsBadRequest() {
        var error = assertThrows(ResponseStatusException.class, () -> MovieTab.parse("unknown"));
        assertEquals(BAD_REQUEST, error.getStatusCode());
    }
}
