package com.georgiytitov.movies.movie;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum MovieTab {
  now_playing,
  upcoming,
  top_rated;

  private static final String INVALID_TAB = "tab must be now_playing, upcoming or top_rated";

  public static MovieTab parse(String value) {
    try {
      return valueOf(value);
    } catch (IllegalArgumentException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_TAB);
    }
  }
}
