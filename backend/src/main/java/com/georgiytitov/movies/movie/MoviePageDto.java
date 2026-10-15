package com.georgiytitov.movies.movie;

import java.util.List;

public record MoviePageDto(int page, int totalPages, List<MovieDto> movies) {
}
