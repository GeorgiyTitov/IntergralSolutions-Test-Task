package com.georgiytitov.movies.favorite;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddFavoriteRequest(@NotNull @Positive Long movieId) {
}
