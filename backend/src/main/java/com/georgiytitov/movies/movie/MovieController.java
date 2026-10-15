package com.georgiytitov.movies.movie;

import com.georgiytitov.movies.tmdb.TmdbClient;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final TmdbClient tmdb;

    public MovieController(TmdbClient tmdb) {
        this.tmdb = tmdb;
    }

    @GetMapping
    public MoviePageDto movies(
        @RequestParam(defaultValue = "now_playing") String tab,
        @RequestParam(defaultValue = "1") @Min(1) @Max(MoviePagination.MAX_PAGE) int page
    ) {
        return tmdb.movies(MovieTab.parse(tab), page);
    }

    @GetMapping("/{id}")
    public MovieDto movie(@PathVariable @Min(1) long id) {
        return tmdb.movie(id);
    }
}
