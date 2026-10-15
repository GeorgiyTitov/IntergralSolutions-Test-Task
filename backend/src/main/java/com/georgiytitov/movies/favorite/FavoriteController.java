package com.georgiytitov.movies.favorite;

import com.georgiytitov.movies.movie.MovieDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/favorites")
public class FavoriteController {
  private final FavoriteService favorites;

  public FavoriteController(FavoriteService favorites) {
    this.favorites = favorites;
  }

  @GetMapping
  public List<MovieDto> list(@PathVariable UUID userId) {
    return favorites.list(userId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public MovieDto add(@PathVariable UUID userId, @Valid @RequestBody AddFavoriteRequest request) {
    return favorites.add(userId, request.movieId());
  }

  @DeleteMapping("/{movieId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void remove(@PathVariable UUID userId, @PathVariable @Min(1) long movieId) {
    favorites.remove(userId, movieId);
  }
}
