package com.georgiytitov.movies.favorite;

import com.georgiytitov.movies.movie.MovieDto;
import com.georgiytitov.movies.tmdb.TmdbClient;
import java.util.List;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FavoriteService {
  private static final String DUPLICATE_FAVORITE = "Movie is already a favorite";
  private static final String FAVORITE_NOT_FOUND = "Favorite not found";

  private final FavoriteRepository favorites;
  private final TmdbClient tmdb;

  public FavoriteService(FavoriteRepository favorites, TmdbClient tmdb) {
    this.favorites = favorites;
    this.tmdb = tmdb;
  }

  public List<MovieDto> list(UUID userId) {
    return favorites.findAll(userId);
  }

  public MovieDto add(UUID userId, long movieId) {
    MovieDto movie = tmdb.movie(movieId);
    try {
      favorites.save(userId, movie);
    } catch (DuplicateKeyException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, DUPLICATE_FAVORITE);
    }
    return movie;
  }

  public void remove(UUID userId, long movieId) {
    if (!favorites.remove(userId, movieId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, FAVORITE_NOT_FOUND);
    }
  }
}
